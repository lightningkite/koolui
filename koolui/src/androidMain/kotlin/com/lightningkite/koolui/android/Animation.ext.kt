package com.lightningkite.koolui.android

import com.lightningkite.koolui.concepts.Animation


fun Animation.android(): AnimationSet = when (this) {
    Animation.None -> AnimationSet({ animate().alpha(1f).setDuration(1) }, { animate().alpha(0f).setDuration(1) })
    Animation.Push -> AnimationSet.slidePush
    Animation.Pop -> AnimationSet.slidePop
    Animation.MoveUp -> AnimationSet.slideDown
    Animation.MoveDown -> AnimationSet.slideUp
    Animation.Fade -> AnimationSet.fade
    Animation.Flip -> AnimationSet.flipVertical
}