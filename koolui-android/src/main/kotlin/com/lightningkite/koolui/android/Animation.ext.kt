package com.lightningkite.koolui.android

import com.lightningkite.koolui.concepts.Animation


fun Animation.android(): AnimationSet = when (this) {
    Animation.None -> AnimationSet({ animate().setDuration(0) }, { animate().setDuration(0) })
    Animation.Push -> AnimationSet.slidePush
    Animation.Pop -> AnimationSet.slidePop
    Animation.MoveUp -> AnimationSet.slideDown
    Animation.MoveDown -> AnimationSet.slideUp
    Animation.Fade -> AnimationSet.fade
    Animation.Flip -> AnimationSet.flipVertical
}