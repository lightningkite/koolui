package com.larvalabs.svgandroid

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.ColorFilter
import android.util.Log

import com.larvalabs.svgandroid.SVGParser.SVGHandler

import org.xml.sax.InputSource

import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 * Builder for reading SVGs. Specify input, specify any parsing options (optional), then call [.build] to parse
 * and return a [SVG].
 *
 * @since 24/12/2012
 */
class SVGBuilder {
    private var data: InputStream? = null
    private var searchColor: Int? = null
    private var replaceColor: Int? = null
    private var strokeColorFilter: ColorFilter? = null
    private var fillColorFilter: ColorFilter? = null
    private var whiteMode = false
    private var overideOpacity = false
    private var closeInputStream = true

    /**
     * Parse SVG data from an input stream.
     *
     * @param svgData the input stream, with SVG XML data in UTF-8 character encoding.
     * @return the parsed SVG.
     */
    fun readFromInputStream(svgData: InputStream): SVGBuilder {
        this.data = svgData
        return this
    }

    /**
     * Parse SVG data from a string.
     *
     * @param svgData the string containing SVG XML data.
     */
    fun readFromString(svgData: String): SVGBuilder {
        this.data = ByteArrayInputStream(svgData.toByteArray())
        return this
    }

    /**
     * Parse SVG data from an Android application resource.
     *
     * @param resources the Android context resources.
     * @param resId     the ID of the raw resource SVG.
     */
    fun readFromResource(resources: Resources, resId: Int): SVGBuilder {
        this.data = resources.openRawResource(resId)
        return this
    }

    /**
     * Parse SVG data from an Android application asset.
     *
     * @param assetMngr the Android asset manager.
     * @param svgPath   the path to the SVG file in the application's assets.
     * @throws IOException if there was a problem reading the file.
     */
    @Throws(IOException::class)
    fun readFromAsset(assetMngr: AssetManager, svgPath: String): SVGBuilder {
        this.data = assetMngr.open(svgPath)
        return this
    }

    fun clearColorSwap(): SVGBuilder {
        replaceColor = null
        searchColor = replaceColor
        return this
    }

    /**
     * Replaces a single colour with another, affecting the opacity.
     *
     * @param searchColor    The colour in the SVG.
     * @param replaceColor   The desired colour.
     * @param overideOpacity If true, combines the opacity defined in the SVG resource with the alpha of replaceColor.
     */
    @JvmOverloads
    fun setColorSwap(searchColor: Int, replaceColor: Int, overideOpacity: Boolean = false): SVGBuilder {
        this.searchColor = searchColor
        this.replaceColor = replaceColor
        this.overideOpacity = overideOpacity
        return this
    }

    /**
     * In white-mode, fills are drawn in white and strokes are not drawn at all.
     */
    fun setWhiteMode(whiteMode: Boolean): SVGBuilder {
        this.whiteMode = whiteMode
        return this
    }

    /**
     * Applies a [ColorFilter] to the paint objects used to render the SVG.
     */
    fun setColorFilter(colorFilter: ColorFilter): SVGBuilder {
        this.fillColorFilter = colorFilter
        this.strokeColorFilter = this.fillColorFilter
        return this
    }

    /**
     * Applies a [ColorFilter] to strokes in the SVG.
     */
    fun setStrokeColorFilter(colorFilter: ColorFilter): SVGBuilder {
        this.strokeColorFilter = colorFilter
        return this
    }

    /**
     * Applies a [ColorFilter] to fills in the SVG.
     */
    fun setFillColorFilter(colorFilter: ColorFilter): SVGBuilder {
        this.fillColorFilter = colorFilter
        return this
    }

    /**
     * Whether or not to close the input stream after reading (ie. after calling [.build].<br></br>
     * *(default is true)*
     */
    fun setCloseInputStreamWhenDone(closeInputStream: Boolean): SVGBuilder {
        this.closeInputStream = closeInputStream
        return this
    }

    /**
     * Loads, reads, parses the SVG (or SVGZ).
     *
     * @return the parsed SVG.
     * @throws SVGParseException if there is an error while parsing.
     */
    @Throws(SVGParseException::class)
    fun build(): SVG {
        if (data == null) {
            throw IllegalStateException("SVG input not specified. Call one of the readFrom...() methods first.")
        }

        try {
            val handler = SVGHandler()
            handler.setColorSwap(searchColor, replaceColor, overideOpacity)
            handler.setWhiteMode(whiteMode)
            if (strokeColorFilter != null) {
                handler.strokePaint.colorFilter = strokeColorFilter
            }
            if (fillColorFilter != null) {
                handler.fillPaint.colorFilter = fillColorFilter
            }

            // SVGZ support (based on https://github.com/josefpavlik/svg-android/commit/fc0522b2e1):
            if (!data!!.markSupported())
                data = BufferedInputStream(data!!) // decorate stream so we can use mark/reset
            try {
                data!!.mark(4)
                val magic = ByteArray(2)
                val r = data!!.read(magic, 0, 2)
                val magicInt = magic[0] + (magic[1].toInt() shl 8) and 0xffff
                data!!.reset()
                if (r == 2 && magicInt == GZIPInputStream.GZIP_MAGIC) {
                    // Log.d(SVGParser.TAG, "SVG is gzipped");
                    val gin = GZIPInputStream(data)
                    data = gin
                }
            } catch (ioe: IOException) {
                throw SVGParseException(ioe)
            }

            return SVGParser.parse(InputSource(data), handler)

        } finally {
            if (closeInputStream) {
                try {
                    data!!.close()
                } catch (e: IOException) {
                    Log.e(SVGParser.TAG, "Error closing SVG input stream.", e)
                }

            }
        }
    }
}
/**
 * Replaces a single colour with another.
 *
 * @param searchColor  The colour in the SVG.
 * @param replaceColor The desired colour.
 */
