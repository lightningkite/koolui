package com.larvalabs.svgandroid

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Picture
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.util.Log

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.SAXNotRecognizedException
import org.xml.sax.helpers.DefaultHandler

import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedList
import java.util.StringTokenizer
import java.util.regex.Pattern

import javax.xml.parsers.SAXParserFactory

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

/**
 * @author Larva Labs, LLC
 */
object SVGParser {

    internal val TAG = "SVGAndroid"

    private var DISALLOW_DOCTYPE_DECL = true

    private val TRANSFORM_SEP = Pattern.compile("[\\s,]*")

    private val arcRectf = RectF()
    private val arcMatrix = Matrix()
    private val arcMatrix2 = Matrix()

    /**
     * Parses a single SVG path and returns it as a `android.graphics.Path` object. An example path is
     * `M250,150L150,350L350,350Z`, which draws a triangle.
     *
     * @param pathString the SVG path, see the specification [here](http://www.w3.org/TR/SVG/paths.html).
     */
    fun parsePath(pathString: String): Path {
        return doPath(pathString)
    }

    @Throws(SVGParseException::class)
    internal fun parse(data: InputSource, handler: SVGHandler): SVG {
        try {
            val picture = Picture()
            handler.setPicture(picture)

            val spf = SAXParserFactory.newInstance()
            val sp = spf.newSAXParser()
            val xr = sp.xmlReader
            xr.contentHandler = handler
            xr.setFeature("http://xml.org/sax/features/validation", false)
            if (DISALLOW_DOCTYPE_DECL) {
                try {
                    xr.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
                } catch (e: SAXNotRecognizedException) {
                    DISALLOW_DOCTYPE_DECL = false
                }

            }
            xr.parse(data)

            val result = SVG(picture, handler.bounds)
            // Skip bounds if it was an empty pic
            if (!java.lang.Float.isInfinite(handler.limits.top)) {
                result.limits = (handler.limits)
            }
            return result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse SVG.", e)
            throw SVGParseException(e)
        }

    }

    private fun parseNumbers(s: String): NumberParse {
        // Util.debug("Parsing numbers from: '" + s + "'");
        val n = s.length
        var p = 0
        val numbers = ArrayList<Float>()
        var skipChar = false
        var prevWasE = false
        for (i in 1 until n) {
            if (skipChar) {
                skipChar = false
                continue
            }
            val c = s[i]
            when (c) {
                // This ends the parsing, as we are on the next element
                'M', 'm', 'Z', 'z', 'L', 'l', 'H', 'h', 'V', 'v', 'C', 'c', 'S', 's', 'Q', 'q', 'T', 't', 'a', 'A', ')' -> {
                    val str = s.substring(p, i)
                    if (str.trim { it <= ' ' }.length > 0) {
                        // Util.debug("  Last: " + str);
                        val f = java.lang.Float.parseFloat(str)
                        numbers.add(f)
                    }
                    p = i
                    return NumberParse(numbers, p)
                }
                '-' -> {
                    // Allow numbers with negative exp such as 7.23e-4
                    if (prevWasE) {
                        prevWasE = false
                    } else {
                        val str = s.substring(p, i)
                        // Just keep moving if multiple whitespace
                        if (str.trim { it <= ' ' }.isNotEmpty()) {
                            // Util.debug("  Next: " + str);
                            val f = java.lang.Float.parseFloat(str)
                            numbers.add(f)
                            if (c == '-') {
                                p = i
                            } else {
                                p = i + 1
                                skipChar = true
                            }
                        } else {
                            p++
                        }
                        prevWasE = false
                    }
                }
                // fall-through
                '\n', '\t', ' ', ',' -> {
                    val str = s.substring(p, i)
                    if (str.trim { it <= ' ' }.isNotEmpty()) {
                        val f = java.lang.Float.parseFloat(str)
                        numbers.add(f)
                        if (c == '-') {
                            p = i
                        } else {
                            p = i + 1
                            skipChar = true
                        }
                    } else {
                        p++
                    }
                    prevWasE = false
                }
                'e' -> prevWasE = true
                else -> prevWasE = false
            }
        }

        val last = s.substring(p)
        if (last.length > 0) {
            // Util.debug("  Last: " + last);
            try {
                numbers.add(java.lang.Float.parseFloat(last))
            } catch (nfe: NumberFormatException) {
                // Just white-space, forget it
            }

            p = s.length
        }
        return NumberParse(numbers, p)
    }

    /**
     * Parse a list of transforms such as: foo(n,n,n...) bar(n,n,n..._ ...) Delimiters are whitespaces or commas
     */
    private fun parseTransform(s: String?): Matrix {
        var s = s
        val matrix = Matrix()
        while (true) {
            parseTransformItem(s!!, matrix)
            // Log.i(TAG, "Transformed: (" + s + ") " + matrix);
            val rparen = s.indexOf(")")
            if (rparen > 0 && s.length > rparen + 1) {
                s = TRANSFORM_SEP.matcher(s.substring(rparen + 1)).replaceFirst("")
            } else {
                break
            }
        }
        return matrix
    }

    private fun parseTransformItem(s: String, matrix: Matrix): Matrix {
        if (s.startsWith("matrix(")) {
            val np = parseNumbers(s.substring("matrix(".length))
            if (np.numbers.size == 6) {
                val mat = Matrix()
                mat.setValues(floatArrayOf(
                        // Row 1
                        np.numbers[0], np.numbers[2], np.numbers[4],
                        // Row 2
                        np.numbers[1], np.numbers[3], np.numbers[5],
                        // Row 3
                        0f, 0f, 1f))
                matrix.preConcat(mat)
            }
        } else if (s.startsWith("translate(")) {
            val np = parseNumbers(s.substring("translate(".length))
            if (np.numbers.size > 0) {
                val tx = np.numbers[0]
                var ty = 0f
                if (np.numbers.size > 1) {
                    ty = np.numbers[1]
                }
                matrix.preTranslate(tx, ty)
            }
        } else if (s.startsWith("scale(")) {
            val np = parseNumbers(s.substring("scale(".length))
            if (np.numbers.size > 0) {
                val sx = np.numbers[0]
                var sy = sx
                if (np.numbers.size > 1) {
                    sy = np.numbers[1]
                }
                matrix.preScale(sx, sy)
            }
        } else if (s.startsWith("skewX(")) {
            val np = parseNumbers(s.substring("skewX(".length))
            if (np.numbers.size > 0) {
                val angle = np.numbers[0]
                matrix.preSkew(Math.tan(angle.toDouble()).toFloat(), 0f)
            }
        } else if (s.startsWith("skewY(")) {
            val np = parseNumbers(s.substring("skewY(".length))
            if (np.numbers.size > 0) {
                val angle = np.numbers[0]
                matrix.preSkew(0f, Math.tan(angle.toDouble()).toFloat())
            }
        } else if (s.startsWith("rotate(")) {
            val np = parseNumbers(s.substring("rotate(".length))
            if (np.numbers.size > 0) {
                val angle = np.numbers[0]
                var cx = 0f
                var cy = 0f
                if (np.numbers.size > 2) {
                    cx = np.numbers[1]
                    cy = np.numbers[2]
                }
                matrix.preTranslate(-cx, -cy)
                matrix.preRotate(angle)
                matrix.preTranslate(cx, cy)
            }
        } else {
            Log.w(TAG, "Invalid transform ($s)")
        }
        return matrix
    }

    /**
     * This is where the hard-to-parse paths are handled. Uppercase rules are absolute positions, lowercase are
     * relative. Types of path rules:
     *
     *
     *
     *  1. M/m - (x y)+ - Move to (without drawing)
     *  1. Z/z - (no params) - Close path (back to starting point)
     *  1. L/l - (x y)+ - Line to
     *  1. H/h - x+ - Horizontal ine to
     *  1. V/v - y+ - Vertical line to
     *  1. C/c - (x1 y1 x2 y2 x y)+ - Cubic bezier to
     *  1. S/s - (x2 y2 x y)+ - Smooth cubic bezier to (shorthand that assumes the x2, y2 from previous C/S is the x1,
     * y1 of this bezier)
     *  1. Q/q - (x1 y1 x y)+ - Quadratic bezier to
     *  1. T/t - (x y)+ - Smooth quadratic bezier to (assumes previous control point is "reflection" of last one w.r.t.
     * to current point)
     *
     *
     *
     * Numbers are separate by whitespace, comma or nothing at all (!) if they are self-delimiting, (ie. begin with a -
     * sign)
     *
     * @param s the path string from the XML
     */
    private fun doPath(s: String): Path {
        val n = s.length
        val ph = ParserHelper(s, 0)
        ph.skipWhitespace()
        val p = Path()
        var lastX = 0f
        var lastY = 0f
        var lastX1 = 0f
        var lastY1 = 0f
        var subPathStartX = 0f
        var subPathStartY = 0f
        var prevCmd: Char = 0.toChar()
        while (ph.pos < n) {
            var cmd = s[ph.pos]
            when (cmd) {
                '-', '+', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.' -> {
                    if (prevCmd == 'm' || prevCmd == 'M') {
                        //Change to line
                        cmd = (prevCmd.toInt() - 1).toChar()
                    } else if ("lhvcsqta".indexOf(Character.toLowerCase(prevCmd)) >= 0) {
                        cmd = prevCmd
                    } else {
                        ph.advance()
                        prevCmd = cmd
                    }
                }
                else -> {
                    ph.advance()
                    prevCmd = cmd
                }
            }

            var wasCurve = false
            when (cmd) {
                'M', 'm' -> {
                    val x = ph.nextFloat()
                    val y = ph.nextFloat()
                    if (cmd == 'm') {
                        subPathStartX += x
                        subPathStartY += y
                        p.rMoveTo(x, y)
                        lastX += x
                        lastY += y
                    } else {
                        subPathStartX = x
                        subPathStartY = y
                        p.moveTo(x, y)
                        lastX = x
                        lastY = y
                    }
                }
                'Z', 'z' -> {
                    p.close()
                    p.moveTo(subPathStartX, subPathStartY)
                    lastX = subPathStartX
                    lastY = subPathStartY
                    lastX1 = subPathStartX
                    lastY1 = subPathStartY
                    wasCurve = true
                }
                'T', 't',
                    // todo - smooth quadratic Bezier (two parameters)
                'L', 'l' -> {
                    val x = ph.nextFloat()
                    val y = ph.nextFloat()
                    if (cmd == 'l') {
                        p.rLineTo(x, y)
                        lastX += x
                        lastY += y
                    } else {
                        p.lineTo(x, y)
                        lastX = x
                        lastY = y
                    }
                }
                'H', 'h' -> {
                    val x = ph.nextFloat()
                    if (cmd == 'h') {
                        p.rLineTo(x, 0f)
                        lastX += x
                    } else {
                        p.lineTo(x, lastY)
                        lastX = x
                    }
                }
                'V', 'v' -> {
                    val y = ph.nextFloat()
                    if (cmd == 'v') {
                        p.rLineTo(0f, y)
                        lastY += y
                    } else {
                        p.lineTo(lastX, y)
                        lastY = y
                    }
                }
                'C', 'c' -> {
                    wasCurve = true
                    var x1 = ph.nextFloat()
                    var y1 = ph.nextFloat()
                    var x2 = ph.nextFloat()
                    var y2 = ph.nextFloat()
                    var x = ph.nextFloat()
                    var y = ph.nextFloat()
                    if (cmd == 'c') {
                        x1 += lastX
                        x2 += lastX
                        x += lastX
                        y1 += lastY
                        y2 += lastY
                        y += lastY
                    }
                    p.cubicTo(x1, y1, x2, y2, x, y)
                    lastX1 = x2
                    lastY1 = y2
                    lastX = x
                    lastY = y
                }
                'Q', 'q',
                    // todo - quadratic Bezier (four parameters)
                'S', 's' -> {
                    wasCurve = true
                    var x2 = ph.nextFloat()
                    var y2 = ph.nextFloat()
                    var x = ph.nextFloat()
                    var y = ph.nextFloat()
                    if (Character.isLowerCase(cmd)) {
                        x2 += lastX
                        x += lastX
                        y2 += lastY
                        y += lastY
                    }
                    val x1 = 2 * lastX - lastX1
                    val y1 = 2 * lastY - lastY1
                    p.cubicTo(x1, y1, x2, y2, x, y)
                    lastX1 = x2
                    lastY1 = y2
                    lastX = x
                    lastY = y
                }
                'A', 'a' -> {
                    val rx = ph.nextFloat()
                    val ry = ph.nextFloat()
                    val theta = ph.nextFloat()
                    val largeArc = ph.nextFlag()
                    val sweepArc = ph.nextFlag()
                    var x = ph.nextFloat()
                    var y = ph.nextFloat()
                    if (cmd == 'a') {
                        x += lastX
                        y += lastY
                    }
                    drawArc(p, lastX, lastY, x, y, rx, ry, theta, largeArc, sweepArc)
                    lastX = x
                    lastY = y
                }
                else -> {
                    Log.w(TAG, "Invalid path command: $cmd")
                    ph.advance()
                }
            }
            if (!wasCurve) {
                lastX1 = lastX
                lastY1 = lastY
            }
            ph.skipWhitespace()
        }
        return p
    }

    private fun angle(x1: Float, y1: Float, x2: Float, y2: Float): Float {

        return Math.toDegrees(Math.atan2(x1.toDouble(), y1.toDouble()) - Math.atan2(x2.toDouble(), y2.toDouble())).toFloat() % 360
    }

    private fun drawArc(p: Path, lastX: Float, lastY: Float, x: Float, y: Float, rx: Float, ry: Float, theta: Float,
                        largeArc: Int, sweepArc: Int) {
        var rx = rx
        var ry = ry
        // Log.d("drawArc", "from (" + lastX + "," + lastY + ") to (" + x + ","+ y + ") r=(" + rx + "," + ry +
        // ") theta=" + theta + " flags="+ largeArc + "," + sweepArc);

        // http://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes

        if (rx == 0f || ry == 0f) {
            p.lineTo(x, y)
            return
        }

        if (x == lastX && y == lastY) {
            return  // nothing to draw
        }

        rx = Math.abs(rx)
        ry = Math.abs(ry)

        val thrad = theta * Math.PI.toFloat() / 180
        val st = Math.sin(thrad.toDouble()).toFloat()
        val ct = Math.cos(thrad.toDouble()).toFloat()

        val xc = (lastX - x) / 2
        val yc = (lastY - y) / 2
        val x1t = ct * xc + st * yc
        val y1t = -st * xc + ct * yc

        val x1ts = x1t * x1t
        val y1ts = y1t * y1t
        var rxs = rx * rx
        var rys = ry * ry

        val lambda = (x1ts / rxs + y1ts / rys) * 1.001f // add 0.1% to be sure that no out of range occurs due to
        // limited precision
        if (lambda > 1) {
            val lambdasr = Math.sqrt(lambda.toDouble()).toFloat()
            rx *= lambdasr
            ry *= lambdasr
            rxs = rx * rx
            rys = ry * ry
        }

        val R = (Math.sqrt(((rxs * rys - rxs * y1ts - rys * x1ts) / (rxs * y1ts + rys * x1ts)).toDouble()) * if (largeArc == sweepArc) -1 else 1).toFloat()
        val cxt = R * rx * y1t / ry
        val cyt = -R * ry * x1t / rx
        val cx = ct * cxt - st * cyt + (lastX + x) / 2
        val cy = st * cxt + ct * cyt + (lastY + y) / 2

        val th1 = angle(1f, 0f, (x1t - cxt) / rx, (y1t - cyt) / ry)
        var dth = angle((x1t - cxt) / rx, (y1t - cyt) / ry, (-x1t - cxt) / rx, (-y1t - cyt) / ry)

        if (sweepArc == 0 && dth > 0) {
            dth -= 360f
        } else if (sweepArc != 0 && dth < 0) {
            dth += 360f
        }

        // draw
        if (theta % 360 == 0f) {
            // no rotate and translate need
            arcRectf.set(cx - rx, cy - ry, cx + rx, cy + ry)
            p.arcTo(arcRectf, th1, dth)
        } else {
            // this is the hard and slow part :-)
            arcRectf.set(-rx, -ry, rx, ry)

            arcMatrix.reset()
            arcMatrix.postRotate(theta)
            arcMatrix.postTranslate(cx, cy)
            arcMatrix.invert(arcMatrix2)

            p.transform(arcMatrix2)
            p.arcTo(arcRectf, th1, dth)
            p.transform(arcMatrix)
        }
    }

    private fun getNumberParseAttr(name: String, attributes: Attributes): NumberParse? {
        val n = attributes.length
        for (i in 0 until n) {
            if (attributes.getLocalName(i) == name) {
                return parseNumbers(attributes.getValue(i))
            }
        }
        return null
    }

    private fun getStringAttr(name: String, attributes: Attributes): String? {
        val n = attributes.length
        for (i in 0 until n) {
            if (attributes.getLocalName(i) == name) {
                return attributes.getValue(i)
            }
        }
        return null
    }

    private fun getFloatAttr(name: String, attributes: Attributes, defaultValue: Float? = null): Float? {
        val v = getStringAttr(name, attributes)
        return parseFloatValue(v, defaultValue)
    }

    private fun getFloatAttr(name: String, attributes: Attributes, defaultValue: Float): Float {
        val v = getStringAttr(name, attributes)
        return parseFloatValue(v, defaultValue)!!
    }

    private fun parseFloatValue(str: String?, defaultValue: Float?): Float? {
        var str = str
        if (str == null) {
            return defaultValue
        } else if (str.endsWith("px")) {
            str = str.substring(0, str.length - 2)
        } else if (str.endsWith("%")) {
            str = str.substring(0, str.length - 1)
            return java.lang.Float.parseFloat(str) / 100
        }
        // Log.d(TAG, "Float parsing '" + name + "=" + v + "'");
        return java.lang.Float.parseFloat(str) * SVG.MULTIPLIER
    }

    internal class NumberParse(internal val numbers: ArrayList<Float>, val nextCmd: Int) {

        fun getNumber(index: Int): Float {
            return numbers[index]
        }

    }

    class Gradient {
        internal var id: String? = null
        internal var xlink: String? = null
        internal var isLinear: Boolean = false
        internal var x1: Float = 0.toFloat()
        internal var y1: Float = 0.toFloat()
        internal var x2: Float = 0.toFloat()
        internal var y2: Float = 0.toFloat()
        internal var x: Float = 0.toFloat()
        internal var y: Float = 0.toFloat()
        internal var radius: Float = 0.toFloat()
        internal var positions = ArrayList<Float>()
        internal var colors = ArrayList<Int>()
        internal var matrix: Matrix? = null
        var shader: Shader? = null
        var boundingBox = false
        var tilemode: TileMode? = null

        /*
                public Gradient createChild(Gradient g) {
                    Gradient child = new Gradient();
                    child.id = g.id;
                    child.xlink = id;
                    child.isLinear = g.isLinear;
                    child.x1 = g.x1;
                    child.x2 = g.x2;
                    child.y1 = g.y1;
                    child.y2 = g.y2;
                    child.x = g.x;
                    child.y = g.y;
                    child.radius = g.radius;
                    child.positions = positions;
                    child.colors = colors;
                    child.matrix = matrix;
                    if (g.matrix != null) {
                        if (matrix == null) {
                            child.matrix = g.matrix;
                        } else {
                            Matrix m = new Matrix(matrix);
                            m.preConcat(g.matrix);
                            child.matrix = m;
                        }
                    }
                    child.boundingBox = g.boundingBox;
                    child.shader = g.shader;
                    child.tilemode = g.tilemode;
                    return child;
                }
        */
        fun inherit(parent: Gradient) {
            val child = this
            child.xlink = parent.id
            child.positions = parent.positions
            child.colors = parent.colors
            if (child.matrix == null) {
                child.matrix = parent.matrix
            } else if (parent.matrix != null) {
                val m = Matrix(parent.matrix)
                m.preConcat(child.matrix)
                child.matrix = m
            }
        }
    }

    class StyleSet constructor(string: String) {
        internal var styleMap = HashMap<String, String>()

        init {
            val styles = string.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in styles) {
                val style = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (style.size == 2) {
                    styleMap[style[0]] = style[1]
                }
            }
        }

        fun getStyle(name: String): String {
            return styleMap[name]!!
        }
    }

    class Properties constructor(internal var atts: Attributes) {
        internal var styles: StyleSet? = null

        init {
            val styleAttr = getStringAttr("style", atts)
            if (styleAttr != null) {
                styles = StyleSet(styleAttr)
            }
        }

        fun getAttr(name: String): String? {
            var v: String? = null
            if (styles != null) {
                v = styles!!.getStyle(name)
            }
            if (v == null) {
                v = getStringAttr(name, atts)
            }
            return v
        }

        fun getString(name: String): String? {
            return getAttr(name)
        }

        private fun rgb(r: Int, g: Int, b: Int): Int {
            return r and 0xff shl 16 or (g and 0xff shl 8) or (b and 0xff)
        }

        @Throws(NumberFormatException::class)
        private fun parseNum(v: String): Int {
            var v = v
            if (v.endsWith("%")) {
                v = v.substring(0, v.length - 1)
                return Math.round(java.lang.Float.parseFloat(v) / 100 * 255)
            }
            return Integer.parseInt(v)
        }

        fun getColor(name: String?): Int? {
            if (name == null) {
                return null
            } else if (name.startsWith("#")) {
                try { // #RRGGBB or #AARRGGBB
                    return Color.parseColor(name)
                } catch (iae: IllegalArgumentException) {
                    return null
                }

            } else if (name.startsWith("rgb(") && name.endsWith(")")) {
                val values = name.substring(4, name.length - 1).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                try {
                    return rgb(parseNum(values[0]), parseNum(values[1]), parseNum(values[2]))
                } catch (nfe: NumberFormatException) {
                    return null
                } catch (e: ArrayIndexOutOfBoundsException) {
                    return null
                }

            } else {
                return SVGColors.mapColour(name)
            }
        }

        // convert 0xRGB into 0xRRGGBB
        private fun hex3Tohex6(x: Int): Int {
            return (x and 0xF00 shl 8 or (x and 0xF00 shl 12) or (x and 0xF0 shl 4) or (x and 0xF0 shl 8) or (x and 0xF shl 4)
                    or (x and 0xF))
        }

        fun getFloat(name: String, defaultValue: Float): Float {
            val v = getAttr(name)
            return if (v == null) {
                defaultValue
            } else {
                try {
                    java.lang.Float.parseFloat(v)
                } catch (nfe: NumberFormatException) {
                    defaultValue
                }

            }
        }

        @JvmOverloads
        fun getFloat(name: String, defaultValue: Float? = null): Float? {
            val v = getAttr(name)
            return if (v == null) {
                defaultValue
            } else {
                try {
                    java.lang.Float.parseFloat(v)
                } catch (nfe: NumberFormatException) {
                    defaultValue
                }

            }
        }
    }

    class LayerAttributes(val opacity: Float)

    class SVGHandler : DefaultHandler() {

        private var picture: Picture? = null
        private var canvas: Canvas? = null
        private var limitsAdjustmentX: Float? = null
        private var limitsAdjustmentY: Float? = null

        val layerAttributeStack = LinkedList<LayerAttributes>()

        var strokePaint: Paint
        var strokeSet = false
        val strokePaintStack = LinkedList<Paint>()
        val strokeSetStack = LinkedList<Boolean>()

        var fillPaint: Paint
        var fillSet = false
        val fillPaintStack = LinkedList<Paint>()
        val fillSetStack = LinkedList<Boolean>()

        var textPaint: Paint
        var drawCharacters: Boolean = false
        // See http://stackoverflow.com/questions/4567636/java-sax-parser-split-calls-to-characters
        var textBuilder: StringBuilder? = null
        var textX: Float? = null
        var textY: Float? = null
        var newLineCount: Int = 0
        var textSize: Float? = null
        var font_matrix: Matrix? = null

        // Scratch rect (so we aren't constantly making new ones)
        val rect = RectF()
        var bounds: RectF? = null
        val limits = RectF(
                java.lang.Float.POSITIVE_INFINITY, java.lang.Float.POSITIVE_INFINITY, java.lang.Float.NEGATIVE_INFINITY, java.lang.Float.NEGATIVE_INFINITY)

        var searchColor: Int? = null
        var replaceColor: Int? = null
        var opacityMultiplier: Float? = null

        var whiteModeField = false

        var canvasRestoreCount: Int? = null

        val transformStack = LinkedList<Boolean>()
        val matrixStack = LinkedList<Matrix>()

        val gradientMapField = HashMap<String, Gradient>()
        var gradientField: Gradient? = null

        private val gradMatrix = Matrix()

        private var hidden = false
        private var hiddenLevel = 0
        private var boundsMode = false

        private val tmpLimitRect = RectF()

        private var SVG_FILL: String? = null

        /**
         * Return full text value for 'text' element.
         * You can override this method if you want replace text before drawing.
         * See [here](http://stackoverflow.com/questions/4567636/java-sax-parser-split-calls-to-characters) for details.
         */
        protected val fullText: String
            get() {
                if (textBuilder == null) {
                    throw IllegalStateException("Must be called only if current element is 'text' and when the value is fully parsed!")
                }
                return textBuilder!!.toString()
            }

        init {
            strokePaint = Paint()
            strokePaint.isAntiAlias = true
            strokePaint.style = Paint.Style.STROKE
            fillPaint = Paint()
            fillPaint.isAntiAlias = true
            fillPaint.style = Paint.Style.FILL
            textPaint = Paint()
            textPaint.isAntiAlias = true
            matrixStack.addFirst(Matrix())
            layerAttributeStack.addFirst(LayerAttributes(1f))
        }

        fun setPicture(picture: Picture) {
            this.picture = picture
        }

        fun setColorSwap(searchColor: Int?, replaceColor: Int?, overideOpacity: Boolean) {
            this.searchColor = searchColor
            this.replaceColor = replaceColor
            if (replaceColor != null && overideOpacity) {
                opacityMultiplier = (replaceColor shr 24 and 0x000000FF) / 255f
            } else {
                opacityMultiplier = null
            }
        }

        fun setWhiteMode(whiteMode: Boolean) {
            this.whiteModeField = whiteMode
        }

        @Throws(SAXException::class)
        override fun startDocument() {
            // Set up prior to parsing a doc
        }

        @Throws(SAXException::class)
        override fun endDocument() {
            // Clean up after parsing a doc
        }

        private fun doFill(atts: Properties, bounding_box: RectF?): Boolean {
            if ("none" == atts.getString("display")) {
                return false
            }
            if (whiteModeField) {
                fillPaint.shader = null
                fillPaint.color = Color.WHITE
                return true
            }
            var fillString = atts.getString("fill")
            if (fillString == null && SVG_FILL != null) {
                fillString = SVG_FILL
            }
            if (fillString != null) {
                if (fillString.startsWith("url(#")) {

                    // It's a gradient fill, look it up in our map
                    val id = fillString.substring("url(#".length, fillString.length - 1)
                    val g = gradientMapField[id]
                    var shader: Shader? = null
                    if (g != null) {
                        shader = g.shader
                    }
                    if (shader != null) {
                        // Util.debug("Found shader!");
                        fillPaint.shader = shader
                        gradMatrix.set(g!!.matrix)
                        if (g.boundingBox && bounding_box != null) {
                            // Log.d("svg", "gradient is bounding box");
                            gradMatrix.preTranslate(bounding_box.left, bounding_box.top)
                            gradMatrix.preScale(bounding_box.width(), bounding_box.height())
                        }
                        shader.setLocalMatrix(gradMatrix)
                        return true
                    } else {
                        Log.w(TAG, "Didn't find shader, using black: $id")
                        fillPaint.shader = null
                        doColor(atts, Color.BLACK, true, fillPaint)
                        return true
                    }
                } else if (fillString.equals("none", ignoreCase = true)) {
                    fillPaint.shader = null
                    fillPaint.color = Color.TRANSPARENT
                    return true
                } else {
                    fillPaint.shader = null
                    val color = atts.getColor(fillString)
                    if (color != null) {
                        doColor(atts, color, true, fillPaint)
                        return true
                    } else {
                        Log.w(TAG, "Unrecognized fill color, using black: $fillString")
                        doColor(atts, Color.BLACK, true, fillPaint)
                        return true
                    }
                }
            } else {
                if (fillSet) {
                    // If fill is set, inherit from parent
                    return fillPaint.color != Color.TRANSPARENT // optimization
                } else {
                    // Default is black fill
                    fillPaint.shader = null
                    fillPaint.color = Color.BLACK
                    return true
                }
            }
        }

        private fun doStroke(atts: Properties): Boolean {
            if (whiteModeField) {
                // Never stroke in white mode
                return false
            }
            if ("none" == atts.getString("display")) {
                return false
            }

            // Check for other stroke attributes
            val width = atts.getFloat("stroke-width")
            if (width != null) {
                strokePaint.strokeWidth = width
            }

            val linecap = atts.getString("stroke-linecap")
            if ("round" == linecap) {
                strokePaint.strokeCap = Paint.Cap.ROUND
            } else if ("square" == linecap) {
                strokePaint.strokeCap = Paint.Cap.SQUARE
            } else if ("butt" == linecap) {
                strokePaint.strokeCap = Paint.Cap.BUTT
            }

            val linejoin = atts.getString("stroke-linejoin")
            if ("miter" == linejoin) {
                strokePaint.strokeJoin = Paint.Join.MITER
            } else if ("round" == linejoin) {
                strokePaint.strokeJoin = Paint.Join.ROUND
            } else if ("bevel" == linejoin) {
                strokePaint.strokeJoin = Paint.Join.BEVEL
            }

            pathStyleHelper(atts.getString("stroke-dasharray"), atts.getString("stroke-dashoffset"))

            val strokeString = atts.getAttr("stroke")
            if (strokeString != null) {
                if (strokeString.equals("none", ignoreCase = true)) {
                    strokePaint.color = Color.TRANSPARENT
                    return false
                } else {
                    val color = atts.getColor(strokeString)
                    if (color != null) {
                        doColor(atts, color, false, strokePaint)
                        return true
                    } else {
                        Log.w(TAG, "Unrecognized stroke color, using none: $strokeString")
                        strokePaint.color = Color.TRANSPARENT
                        return false
                    }
                }
            } else {
                if (strokeSet) {
                    // Inherit from parent
                    return strokePaint.color != Color.TRANSPARENT // optimization
                } else {
                    // Default is none
                    strokePaint.color = Color.TRANSPARENT
                    return false
                }
            }
        }

        private fun doGradient(isLinear: Boolean, atts: Attributes): Gradient {
            val gradient = Gradient()
            gradient.id = getStringAttr("id", atts)
            gradient.isLinear = isLinear
            if (isLinear) {
                gradient.x1 = getFloatAttr("x1", atts, 0f)
                gradient.x2 = getFloatAttr("x2", atts, 1f)
                gradient.y1 = getFloatAttr("y1", atts, 0f)
                gradient.y2 = getFloatAttr("y2", atts, 0f)
            } else {
                gradient.x = getFloatAttr("cx", atts, 0f)
                gradient.y = getFloatAttr("cy", atts, 0f)
                gradient.radius = getFloatAttr("r", atts, 0f)
            }
            val transform = getStringAttr("gradientTransform", atts)
            if (transform != null) {
                gradient.matrix = parseTransform(transform)
            }
            var spreadMethod = getStringAttr("spreadMethod", atts)
            if (spreadMethod == null) {
                spreadMethod = "pad"
            }

            gradient.tilemode = if (spreadMethod == "reflect")
                TileMode.MIRROR
            else if (spreadMethod == "repeat") TileMode.REPEAT else TileMode.CLAMP

            var unit = getStringAttr("gradientUnits", atts)
            if (unit == null) {
                unit = "objectBoundingBox"
            }
            gradient.boundingBox = unit != "userSpaceOnUse"

            var xlink = getStringAttr("href", atts)
            if (xlink != null) {
                if (xlink.startsWith("#")) {
                    xlink = xlink.substring(1)
                }
                gradient.xlink = xlink
            }
            return gradient
        }

        private fun finishGradients() {
            for (gradient in gradientMapField.values) {
                if (gradient.xlink != null) {
                    val parent = gradientMapField[gradient.xlink!!]
                    if (parent != null) {
                        gradient.inherit(parent)
                    }
                }
                val colors = IntArray(gradient.colors.size)
                for (i in colors.indices) {
                    colors[i] = gradient.colors[i]
                }
                val positions = FloatArray(gradient.positions.size)
                for (i in positions.indices) {
                    positions[i] = gradient.positions[i]
                }
                if (colors.size == 0) {
                    Log.d("BAD", "BAD gradientField, id=" + gradient.id!!)
                }
                if (gradient.isLinear) {
                    gradient.shader = LinearGradient(gradient.x1, gradient.y1, gradient.x2, gradient.y2, colors, positions, gradient.tilemode!!)
                } else {
                    gradient.shader = RadialGradient(gradient.x, gradient.y, gradient.radius, colors, positions, gradient.tilemode!!)
                }
            }
        }

        private fun doColor(atts: Properties, color: Int, fillMode: Boolean, paint: Paint) {
            var c = 0xFFFFFF and color or -0x1000000
            if (searchColor != null && searchColor == c) {
                c = replaceColor!!
            }
            paint.shader = null
            paint.color = c
            var opacityAttr = atts.getFloat("opacity")
            if (opacityAttr == null) {
                opacityAttr = atts.getFloat(if (fillMode) "fill-opacity" else "stroke-opacity")
            }

            var opacity = opacityAttr ?: 1f
            opacity *= currentLayerAttributes().opacity
            if (opacityMultiplier != null) {
                opacity *= opacityMultiplier!!
            }
            paint.alpha = (255f * opacity).toInt()
        }

        /**
         * set the path style (if any) stroke-dasharray="n1,n2,..." stroke-dashoffset=n
         */
        private fun pathStyleHelper(style: String?, offset: String?) {
            if (style == null) {
                return
            }

            if (style == "none") {
                strokePaint.pathEffect = null
                return
            }

            val st = StringTokenizer(style, " ,")
            val count = st.countTokens()
            val intervals = FloatArray(if (count and 1 == 1) count * 2 else count)
            var max = 0f
            var current = 1f
            var i = 0
            while (st.hasMoreTokens()) {
                current = toFloat(st.nextToken(), current)
                intervals[i++] = current
                max += current
            }

            // in svg speak, we double the intervals on an odd count
            var start = 0
            while (i < intervals.size) {
                intervals[i] = intervals[start]
                max += intervals[i]
                i++
                start++
            }

            var off = 0f
            if (offset != null) {
                try {
                    off = java.lang.Float.parseFloat(offset) % max
                } catch (e: NumberFormatException) {
                    // ignore
                }

            }

            strokePaint.pathEffect = DashPathEffect(intervals, off)
        }

        private fun toFloat(s: String, dflt: Float): Float {
            var result = dflt
            try {
                result = java.lang.Float.parseFloat(s)
            } catch (e: NumberFormatException) {
                // ignore
            }

            return result
        }

        private fun doLimits2(x: Float, y: Float) {
            if (x < limits.left) {
                limits.left = x
            }
            if (x > limits.right) {
                limits.right = x
            }
            if (y < limits.top) {
                limits.top = y
            }
            if (y > limits.bottom) {
                limits.bottom = y
            }
        }

        private fun doLimits(box: RectF, paint: Paint? = null) {
            val m = matrixStack.last
            m.mapRect(tmpLimitRect, box)
            val width2 = if (paint == null) 0f else paint.strokeWidth / 2f
            doLimits2(tmpLimitRect.left - width2, tmpLimitRect.top - width2)
            doLimits2(tmpLimitRect.right + width2, tmpLimitRect.bottom + width2)
        }

        private fun pushTransform(atts: Attributes) {
            val transform = getStringAttr("transform", atts)
            val pushed = transform != null
            transformStack.addLast(pushed)
            if (pushed) {
                val matrix = parseTransform(transform)
                canvas!!.save()
                canvas!!.concat(matrix)
                matrix.postConcat(matrixStack.last)
                matrixStack.addLast(matrix)
            }

        }

        private fun popTransform() {
            if (transformStack.removeLast()) {
                canvas!!.restore()
                matrixStack.removeLast()
            }
        }

        @Throws(SAXException::class)
        override fun startElement(namespaceURI: String, localName: String, qName: String, atts: Attributes) {
            // Reset paint opacity
            strokePaint.alpha = 255
            fillPaint.alpha = 255
            textPaint.alpha = 255

            this.drawCharacters = false

            // Ignore everything but rectangles in bounds mode
            if (boundsMode) {
                if (localName == "rect") {
                    var x = getFloatAttr("x", atts)
                    if (x == null) {
                        x = 0f
                    }
                    var y = getFloatAttr("y", atts)
                    if (y == null) {
                        y = 0f
                    }
                    val width = getFloatAttr("width", atts)
                    val height = getFloatAttr("height", atts)
                    bounds = RectF(x!!, y!!, x + width!!, y + height!!)
                }
                return
            }
            if (localName == "svg") {
                canvas = null
                SVG_FILL = getStringAttr("fill", atts)
                val viewboxStr = getStringAttr("viewBox", atts)
                if (viewboxStr != null) {
                    val dims = viewboxStr.replace(',', ' ').split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (dims.size == 4) {
                        val x1 = parseFloatValue(dims[0], null)
                        val y1 = parseFloatValue(dims[1], null)
                        var x2 = parseFloatValue(dims[2], null)
                        var y2 = parseFloatValue(dims[3], null)
                        if (x1 != null && x2 != null && y1 != null && y2 != null) {
                            x2 += x1
                            y2 += y1

                            val width = Math.ceil((x2 - x1).toDouble()).toFloat()
                            val height = Math.ceil((y2 - y1).toDouble()).toFloat()
                            canvas = picture!!.beginRecording(width.toInt(), height.toInt())
                            canvasRestoreCount = canvas!!.save()
                            canvas!!.clipRect(0f, 0f, width, height)
                            limitsAdjustmentX = -x1
                            limitsAdjustmentY = -y1
                            canvas!!.translate(limitsAdjustmentX!!, limitsAdjustmentY!!)
                        }
                    }
                }
                // No viewbox
                if (canvas == null) {
                    val width = Math.ceil(getFloatAttr("width", atts)!!.toDouble()).toInt()
                    val height = Math.ceil(getFloatAttr("height", atts)!!.toDouble()).toInt()
                    canvas = picture!!.beginRecording(width, height)
                    canvasRestoreCount = null
                }

            } else if (localName == "defs") {
                // Ignore
            } else if (localName == "linearGradient") {
                gradientField = doGradient(true, atts)
            } else if (localName == "radialGradient") {
                gradientField = doGradient(false, atts)
            } else if (localName == "stop") {
                if (gradientField != null) {
                    val props = Properties(atts)

                    val colour: Int
                    val stopColour = props.getColor(props.getAttr("stop-color"))
                    if (stopColour == null) {
                        colour = 0
                    } else {
                        val alpha = props.getFloat("stop-opacity")
                        if (alpha != null) {
                            val alphaInt = Math.round(255f * alpha * currentLayerAttributes().opacity)
                            // wipe the auto FF opacity from stopColour before applying stop-opacity:
                            colour = stopColour and 0xFFFFFF or (alphaInt shl 24)
                        } else {
                            colour = stopColour
                        }
                    }
                    gradientField!!.colors.add(colour)

                    val offset = props.getFloat("offset", 0f)
                    gradientField!!.positions.add(offset)
                }
            } else if (localName == "g") {
                val props = Properties(atts)

                // Check to see if this is the "bounds" layer
                if ("bounds".equals(getStringAttr("id", atts)!!, ignoreCase = true)) {
                    boundsMode = true
                }
                if (hidden) {
                    hiddenLevel++
                    // Util.debug("Hidden up: " + hiddenLevel);
                }
                // Go in to hidden mode if display is "none"
                if ("none" == getStringAttr("display", atts) || "none" == props.getString("display")) {
                    if (!hidden) {
                        hidden = true
                        hiddenLevel = 1
                        // Util.debug("Hidden up: " + hiddenLevel);
                    }
                }

                // Create layer attributes
                val opacity = props.getFloat("opacity", 1f)
                val curLayerAttr = currentLayerAttributes()
                val newLayerAttr = LayerAttributes(curLayerAttr.opacity * opacity)
                layerAttributeStack.addLast(newLayerAttr)

                pushTransform(atts)
                fillPaintStack.addLast(Paint(fillPaint))
                strokePaintStack.addLast(Paint(strokePaint))
                fillSetStack.addLast(fillSet)
                strokeSetStack.addLast(strokeSet)

                fillSet = fillSet or doFill(props, null) // Added by mrn but a boundingBox is now required by josef.
                strokeSet = strokeSet or doStroke(props)
            } else if (!hidden && localName == "rect") {
                var x = getFloatAttr("x", atts)
                if (x == null) {
                    x = 0f
                }
                var y = getFloatAttr("y", atts)
                if (y == null) {
                    y = 0f
                }
                val width = getFloatAttr("width", atts)
                val height = getFloatAttr("height", atts)
                val rx = getFloatAttr("rx", atts, 0f)
                val ry = getFloatAttr("ry", atts, 0f)
                pushTransform(atts)
                val props = Properties(atts)
                rect.set(x!!, y!!, x + width!!, y + height!!)
                if (doFill(props, rect)) {
                    rect.set(x, y, x + width, y + height)
                    if (rx <= 0f && ry <= 0f) {
                        canvas!!.drawRect(rect, fillPaint)
                    } else {
                        canvas!!.drawRoundRect(rect, rx, ry, fillPaint)
                    }
                    doLimits(rect)
                }
                if (doStroke(props)) {
                    rect.set(x, y, x + width, y + height)
                    if (rx <= 0f && ry <= 0f) {
                        canvas!!.drawRect(rect, strokePaint)
                    } else {
                        canvas!!.drawRoundRect(rect, rx, ry, strokePaint)
                    }
                    doLimits(rect, strokePaint)
                }
                popTransform()
            } else if (!hidden && localName == "line") {
                val x1 = getFloatAttr("x1", atts)
                val x2 = getFloatAttr("x2", atts)
                val y1 = getFloatAttr("y1", atts)
                val y2 = getFloatAttr("y2", atts)
                val props = Properties(atts)
                if (doStroke(props)) {
                    pushTransform(atts)
                    rect.set(x1!!, y1!!, x2!!, y2!!)
                    canvas!!.drawLine(x1, y1, x2, y2, strokePaint)
                    doLimits(rect, strokePaint)
                    popTransform()
                }
            } else if (!hidden && localName == "text") {
                val textX = getFloatAttr("x", atts)
                val textY = getFloatAttr("y", atts)
                val fontSize = getFloatAttr("font-size", atts)
                val font_matrix = parseTransform(getStringAttr("transform",
                        atts))
                drawCharacters = true
                if (fontSize != null) {
                    textSize = fontSize
                    pushTransform(atts)
                    if (textX != null && textY != null) {
                        this.textX = textX
                        this.textY = textY
                    } else if (font_matrix != null) {
                        this.font_matrix = font_matrix
                    }
                    val props = Properties(atts)
                    val color = props.getColor("fill")
                    if (color != null) {
                        doColor(props, color, true, textPaint)
                    } else {
                        textPaint.color = Color.BLACK
                    }
                    this.newLineCount = 0
                    textPaint.textSize = textSize!!
                    popTransform()
                    canvas!!.save()
                    textBuilder = StringBuilder()
                }
            } else if (!hidden && (localName == "circle" || localName == "ellipse")) {
                val centerX: Float?
                val centerY: Float?
                val radiusX: Float?
                val radiusY: Float?

                centerX = getFloatAttr("cx", atts)
                centerY = getFloatAttr("cy", atts)
                if (localName == "ellipse") {
                    radiusX = getFloatAttr("rx", atts)
                    radiusY = getFloatAttr("ry", atts)

                } else {
                    radiusY = getFloatAttr("r", atts)
                    radiusX = radiusY
                }
                if (centerX != null && centerY != null && radiusX != null && radiusY != null) {
                    pushTransform(atts)
                    val props = Properties(atts)
                    rect.set(centerX - radiusX, centerY - radiusY, centerX + radiusX, centerY + radiusY)
                    if (doFill(props, rect)) {
                        canvas!!.drawOval(rect, fillPaint)
                        doLimits(rect)
                    }
                    if (doStroke(props)) {
                        canvas!!.drawOval(rect, strokePaint)
                        doLimits(rect, strokePaint)
                    }
                    popTransform()
                }
            } else if (!hidden && (localName == "polygon" || localName == "polyline")) {
                val numbers = getNumberParseAttr("points", atts)
                if (numbers != null) {
                    val p = Path()
                    val points = numbers.numbers
                    if (points.size > 1) {
                        pushTransform(atts)
                        val props = Properties(atts)
                        p.moveTo(points[0], points[1])
                        var i = 2
                        while (i < points.size) {
                            val x = points[i]
                            val y = points[i + 1]
                            p.lineTo(x, y)
                            i += 2
                        }
                        // Don't close a polyline
                        if (localName == "polygon") {
                            p.close()
                        }
                        p.computeBounds(rect, false)
                        if (doFill(props, rect)) {
                            canvas!!.drawPath(p, fillPaint)
                            doLimits(rect)
                        }
                        if (doStroke(props)) {
                            canvas!!.drawPath(p, strokePaint)
                            doLimits(rect, strokePaint)
                        }
                        popTransform()
                    }
                }
            } else if (!hidden && localName == "path") {
                val p = doPath(getStringAttr("d", atts)!!)
                pushTransform(atts)
                val props = Properties(atts)
                p.computeBounds(rect, false)
                if (doFill(props, rect)) {
                    canvas!!.drawPath(p, fillPaint)
                    doLimits(rect)
                }
                if (doStroke(props)) {
                    canvas!!.drawPath(p, strokePaint)
                    doLimits(rect, strokePaint)
                }
                popTransform()
            } else if (!hidden) {
                Log.w(TAG, "UNRECOGNIZED SVG COMMAND: $localName")
            }
        }

        fun currentLayerAttributes(): LayerAttributes {
            return layerAttributeStack.last
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            if (this.drawCharacters) {
                val text = String(ch, start, length)
                textBuilder!!.append(text)
                return
            }
        }

        @Throws(SAXException::class)
        override fun endElement(namespaceURI: String, localName: String, qName: String) {
            if (localName == "svg") {
                if (canvasRestoreCount != null) {
                    canvas!!.restoreToCount(canvasRestoreCount!!)
                }
                if (limitsAdjustmentX != null) {
                    limits.left += limitsAdjustmentX!!
                    limits.right += limitsAdjustmentX!!
                }
                if (limitsAdjustmentY != null) {
                    limits.top += limitsAdjustmentY!!
                    limits.bottom += limitsAdjustmentY!!
                }
                picture!!.endRecording()

            } else if (localName == "linearGradient" || localName == "radialGradient") {
                if (gradientField!!.id != null) {
                    gradientMapField[gradientField!!.id!!] = gradientField!!
                }
            } else if (localName == "defs") {
                finishGradients()
            } else if (localName == "g") {
                if (boundsMode) {
                    boundsMode = false
                }
                // Break out of hidden mode
                if (hidden) {
                    hiddenLevel--
                    // Util.debug("Hidden down: " + hiddenLevel);
                    if (hiddenLevel == 0) {
                        hidden = false
                    }
                }
                // // Clear gradient map
                // gradientRefMap.clear();
                popTransform()
                fillPaint = fillPaintStack.removeLast()
                fillSet = fillSetStack.removeLast()
                strokePaint = strokePaintStack.removeLast()
                strokeSet = strokeSetStack.removeLast()
                if (!layerAttributeStack.isEmpty()) {
                    layerAttributeStack.removeLast()
                }
            } else if (localName == "text") {
                if (this.drawCharacters) {
                    val text = fullText
                    if (text.length == 1 && text == "\n") {
                        newLineCount += 1
                        canvas!!.translate(0f, newLineCount * textSize!!)
                    } else {
                        if (this.textX != null && this.textY != null) {
                            canvas!!.drawText(text, this.textX!!, this.textY!!, textPaint)
                        } else {
                            canvas!!.concat(font_matrix)
                            canvas!!.drawText(text, 0f, 0f, textPaint)
                        }
                    }
                    canvas!!.restore()
                    textBuilder = null
                    this.drawCharacters = false
                }
            }
        }
    }
}
