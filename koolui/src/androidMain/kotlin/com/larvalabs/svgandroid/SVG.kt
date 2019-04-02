package com.larvalabs.svgandroid

import android.graphics.Picture
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable

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
 * Describes a vector Picture object, and optionally its boundsInParent.
 *
 * @author Larva Labs, LLC
 */
class SVG
/**
 * Construct a new SVG.
 *
 * @param picture the parsed picture object.
 * @param bounds  the boundsInParent computed from the "boundsInParent" layer in the SVG.
 */
internal constructor(
        /**
         * The parsed Picture object.
         */
        /**
         * Get the parsed SVG picture data.
         *
         * @return the picture.
         */
        val picture: Picture,
        /**
         * These are the boundsInParent for the SVG specified as a hidden "boundsInParent" layer in the SVG.
         */
        /**
         * Gets the bounding rectangle for the SVG, if one was specified.
         *
         * @return rectangle representing the boundsInParent.
         */
        val bounds: RectF?) {

    /**
     * These are the estimated boundsInParent of the SVG computed from the SVG elements while parsing. Note that this could be
     * null if there was a failure to compute limits (ie. an empty SVG).
     */
    /**
     * Gets the bounding rectangle for the SVG that was computed upon parsing. It may not be entirely accurate for
     * certain curves or transformations, but is often better than nothing.
     *
     * @return rectangle representing the computed boundsInParent.
     */
    /**
     * Set the limits of the SVG, which are the estimated boundsInParent computed by the parser.
     *
     * @param limits the boundsInParent computed while parsing the SVG, may not be entirely accurate.
     */
    var limits: RectF? = null
        internal set

    val drawable by lazy {
        SVGDrawable(this)
    }

    companion object {

        const val MULTIPLIER = 8f
    }
}
