package com.larvalabs.svgandroid


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
 * Parses numbers from SVG text. Based on the Batik Number Parser (Apache 2 License).
 *
 * @author Apache Software Foundation, Larva Labs LLC
 */
class ParserHelper(str: String, var pos: Int) {

    private val s: CharArray
    private val n: Int
    private var current: Char = ' '

    init {
        this.s = str.toCharArray()
        n = s.size
        current = s[pos]
    }

    private fun read(): Char {
        if (pos < n) {
            pos++
        }
        return if (pos == n) {
            '\u0000'
        } else {
            s[pos]
        }
    }

    fun skipWhitespace() {
        while (pos < n) {
            if (Character.isWhitespace(s[pos])) {
                advance()
            } else {
                break
            }
        }
    }

    fun skipNumberSeparator() {
        while (pos < n) {
            val c = s[pos]
            when (c) {
                ' ', ',', '\n', '\t' -> advance()
                else -> return
            }
        }
    }

    fun advance() {
        current = read()
    }

    /**
     * Parses the content of the buffer and converts it to a float.
     */
    fun parseFloat(): Float {
        var mant = 0
        var mantDig = 0
        var mantPos = true
        var mantRead = false

        var exp = 0
        var expDig = 0
        var expAdj = 0
        var expPos = true

        when (current) {
            '-' -> {
                mantPos = false
                current = read()
            }
            '+' -> current = read()
        }

        //Skip zeros
        while(true){
            if(current != '0') break
            current = read()
        }

        //Read mantissa
        mantissaLoop@while(true){
            when(current){
                in '0' .. '9' -> {
                    if (mantDig < 9) {
                        mantDig++
                        mant = mant * 10 + (current - '0')
                    } else {
                        expAdj++
                    }
                }
                else -> break@mantissaLoop
            }
            current = read()
        }

        //Read decimal
        if (current == '.') {
            current = read()

            //Read decimal zeroes
            while(true){
                if(current != '0') break
                expAdj--
                current = read()
            }

            //Read decimal
            mantissaLoop@while(true){
                when(current){
                    in '0' .. '9' -> {
                        if (mantDig < 9) {
                            mantDig++
                            mant = mant * 10 + (current - '0')
                            expAdj--
                        }
                    }
                    else -> break@mantissaLoop
                }
                current = read()
            }
        }

        //Read exponent
        if(current == 'e' || current == 'E'){
            current = read()

            //Read positive/negative
            when (current) {
                '-' -> {
                    expPos = false
                    current = read()
                }
                '+' -> {
                    expPos = true
                    current = read()
                }
            }

            //Skip exponent zeroes
            while(true){
                if(current != '0') break
                current = read()
            }

            //Read exponent
            exponentLoop@while(true){
                when(current){
                    in '0' .. '9' -> {
                        if (expDig < 3) {
                            expDig++
                            exp = exp * 10 + (current - '0')
                        }
                    }
                    else -> break@exponentLoop
                }
                current = read()
            }
        }

        if (!expPos) {
            exp = -exp
        }
        exp += expAdj
        if (!mantPos) {
            mant = -mant
        }

        return buildFloat(mant, exp) * SVG.MULTIPLIER
    }

    private fun nextIsNumber(): Boolean {
        current = read()
        return current in '0' .. '9'
    }

    private fun reportUnexpectedCharacterError(c: Char) {
        throw RuntimeException("Unexpected char '$c'.")
    }

    fun nextFloat(): Float {
        skipWhitespace()
        val f = parseFloat()
        skipNumberSeparator()
        return f
    }

    fun nextFlag(): Int {
        skipWhitespace()
        val flag = current - '0'
        current = read()
        skipNumberSeparator()
        return flag
    }

    companion object {

        /**
         * Computes a float from mantissa and exponent.
         */
        fun buildFloat(mant: Int, exp: Int): Float {
            var mant = mant
            if (exp < -125 || mant == 0) {
                return 0.0f
            }

            if (exp >= 128) {
                return if (mant > 0) java.lang.Float.POSITIVE_INFINITY else java.lang.Float.NEGATIVE_INFINITY
            }

            if (exp == 0) {
                return mant.toFloat()
            }

            if (mant >= 1 shl 26) {
                mant++ // round up trailing bits if they will be dropped.
            }

            return (if (exp > 0) mant * pow10[exp] else mant / pow10[-exp]).toFloat()
        }

        /**
         * Array of powers of ten. Using double instead of float gives a tiny bit more precision.
         */
        private val pow10 = DoubleArray(128)

        init {
            for (i in pow10.indices) {
                pow10[i] = Math.pow(10.0, i.toDouble())
            }
        }
    }

}
