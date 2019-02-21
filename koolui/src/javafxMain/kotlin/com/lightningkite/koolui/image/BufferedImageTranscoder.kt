package com.lightningkite.koolui.image

/**
 * This is a compilation of code snippets required to render SVG files in JavaFX using Batik.
 * See my full post on StackOverflow: http://stackoverflow.com/a/23894292/603003
 */

import java.awt.image.BufferedImage
import org.apache.batik.transcoder.TranscoderException
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder

/**
 * Many thanks to bb-generation for sharing this code!
 * @author bb-generation
 * @link https://web.archive.org/web/20131215231214/http://bbgen.net/blog/2011/06/java-svg-to-bufferedimage/
 * @license Unfortunately unknown, but using this code is probably categorized as "fair use" (because the code is in my opinion too simple to be licensed)
 */
class BufferedImageTranscoder : ImageTranscoder() {

    var bufferedImage: BufferedImage? = null
        private set

    override fun createImage(width: Int, height: Int): BufferedImage {
        return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    }

    @Throws(TranscoderException::class)
    override fun writeImage(img: BufferedImage, to: TranscoderOutput?) {
        this.bufferedImage = img
    }
}
