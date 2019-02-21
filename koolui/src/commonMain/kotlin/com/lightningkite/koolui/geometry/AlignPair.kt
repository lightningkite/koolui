package com.lightningkite.koolui.geometry

import com.lightningkite.koolui.geometry.Align.*

enum class AlignPair(val horizontal: Align, val vertical: Align) {
    TopLeft(Start, Start),
    TopCenter(Center, Start),
    TopFill(Fill, Start),
    TopRight(End, Start),
    CenterLeft(Start, Center),
    CenterCenter(Center, Center),
    CenterFill(Fill, Center),
    CenterRight(End, Center),
    FillLeft(Start, Fill),
    FillCenter(Center, Fill),
    FillFill(Fill, Fill),
    FillRight(End, Fill),
    BottomLeft(Start, End),
    BottomCenter(Center, End),
    BottomFill(Fill, End),
    BottomRight(End, End),
}

fun alignPair(horizontal: Align, vertical: Align): AlignPair {
    return when (horizontal) {
        Align.Start -> when (vertical) {
            Align.Start -> AlignPair.TopLeft
            Align.Fill -> AlignPair.FillLeft
            Align.Center -> AlignPair.CenterLeft
            Align.End -> AlignPair.BottomLeft
        }
        Align.Center -> when (vertical) {
            Align.Start -> AlignPair.TopCenter
            Align.Fill -> AlignPair.FillCenter
            Align.Center -> AlignPair.CenterCenter
            Align.End -> AlignPair.BottomCenter
        }
        Align.Fill -> when (vertical) {
            Align.Start -> AlignPair.TopFill
            Align.Fill -> AlignPair.FillFill
            Align.Center -> AlignPair.CenterFill
            Align.End -> AlignPair.BottomFill
        }
        Align.End -> when (vertical) {
            Align.Start -> AlignPair.TopRight
            Align.Fill -> AlignPair.FillRight
            Align.Center -> AlignPair.CenterRight
            Align.End -> AlignPair.BottomRight
        }
    }
}