package com.lightningkite.koolui.image

import com.lightningkite.recktangle.Point
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import org.apache.batik.transcoder.TranscoderException
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscodingHints
import org.apache.batik.transcoder.image.JPEGTranscoder
import java.io.InputStream
import org.apache.batik.gvt.GraphicsNode
import org.apache.batik.bridge.GVTBuilder
import org.apache.batik.bridge.DocumentLoader
import org.apache.batik.bridge.UserAgentAdapter
import org.apache.batik.util.XMLResourceDescriptor
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.bridge.BridgeContext


object SVGRenderer {

    fun getSize(svg: String): Point {
        val factory = SAXSVGDocumentFactory(
            XMLResourceDescriptor.getXMLParserClassName()
        )
        val document = factory.createDocument("i.svg", svg.toByteArray().inputStream())
        val agent = UserAgentAdapter()
        val loader = DocumentLoader(agent)
        val context = BridgeContext(agent, loader)
        context.isDynamic = true
        val builder = GVTBuilder()
        val root = builder.build(context, document)
        return Point(root.primitiveBounds.width.toFloat(), root.primitiveBounds.height.toFloat())
    }

    fun render(svg: String, scale: Float = 1f): WritableImage {
        val defaultSize = getSize(svg)
        return SwingFXUtils.toFXImage(
            BufferedImageTranscoder().let {
                it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, defaultSize.x * scale)
                it.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, defaultSize.y * scale)
                it.transcode(TranscoderInput(svg.toByteArray().inputStream()), null)
                it.bufferedImage
            },
            null
        )
    }

    fun render(svg: String, size: Point): WritableImage {
        return SwingFXUtils.toFXImage(
            BufferedImageTranscoder().let {
                it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, size.x)
                it.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, size.y)
                it.transcode(TranscoderInput(svg.toByteArray().inputStream()), null)
                it.bufferedImage
            },
            null
        )
    }
}
