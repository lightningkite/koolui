package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.geometry.Align
import kotlinx.cinterop.CValue
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.UIKit.NSStringDrawingUsesFontLeading
import platform.UIKit.UILabel
import platform.UIKit.boundingRectWithSize
import kotlin.math.min

class UILabelWithVerticalAlignment : UILabel {

    var verticalAlignment: Align = Align.Center

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    @OverrideInit
    constructor(frame: CValue<CGRect>) : super(frame)

    override fun drawTextInRect(rect: CValue<CGRect>) {
        val attributedText = this.attributedText!!
        rect.useContents {
            val originalHeight = size.height
            size.height = min(
                    attributedText.boundingRectWithSize(rect.useContents { size }.readValue(), NSStringDrawingUsesFontLeading, null).useContents { size.height },
                    numberOfLines * font.lineHeight
            )
            origin.y = when (verticalAlignment) {
                Align.Start -> 0.0
                Align.Fill, Align.Center -> (originalHeight - size.height) / 2
                Align.End -> originalHeight - size.height
            }
        }
        super.drawTextInRect(rect)
    }


}
