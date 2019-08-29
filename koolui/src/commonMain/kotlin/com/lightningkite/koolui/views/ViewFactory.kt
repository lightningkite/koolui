package com.lightningkite.koolui.views

import com.lightningkite.koolui.views.basic.ViewFactoryBasic
import com.lightningkite.koolui.views.dialogs.ViewFactoryDialogs
import com.lightningkite.koolui.views.graphics.ViewFactoryGraphics
import com.lightningkite.koolui.views.interactive.ViewFactoryInteractive
import com.lightningkite.koolui.views.interactive.ViewFactoryInteractiveDefault
import com.lightningkite.koolui.views.layout.ViewFactoryLayout
import com.lightningkite.koolui.views.navigation.ViewFactoryNavigation
import com.lightningkite.koolui.views.navigation.ViewFactoryNavigationDefault
import com.lightningkite.koolui.views.root.ViewFactoryRoot

/**
 * PHILOSOPHY
 *
 * The views will function and appear according to the underlying platform.  Styling does
 * not take place at this interface layer.
 *
 * Thus, every function here is meant to represent a concept rather than a specific widget.
 *
 * This interface is meant to be extended and added upon, and only represents the most basic of
 * views needed for creating an app.
 *
 * Layout does take place at this layer, and is meant to be good at resizing.
 *
 * All views are automatically sized unless stated otherwise - either shrinking as small as possible
 * or growing as large as possible.
 *
 * The defaults for spacing are set to look both clean and good - modify them deliberately.
 *
 * The returned view objects are only meant to be used in composing with other views in the factory.
 * Do not attempt to store references to them long-term or anything of the sort.
 */
interface ViewFactory<VIEW> :
        Themed,
        ViewFactoryBasic<VIEW>,
        ViewFactoryDialogs<VIEW>,
        ViewFactoryGraphics<VIEW>,
        ViewFactoryInteractive<VIEW>,
        ViewFactoryLayout<VIEW>,
        ViewFactoryNavigation<VIEW>,
        ViewFactoryRoot<VIEW> {
    override fun card(view: VIEW): VIEW = frame(view).margin(8f).background(colorSet.backgroundHighlighted)
//    override fun launchSelector(title: String?, options: List<Pair<String, () -> Unit>>) = defaultLaunchSelector(title, options)
//    override fun imageButton(imageWithOptions: ObservableProperty<ImageWithOptions>, label: ObservableProperty<String?>, importance: Importance, onClick: () -> Unit): VIEW = withColorSet(theme.importance(importance)).run {
//        image(imageWithOptions).background(colorSet.background).clickable(onClick)
//    }
//
//    override fun entryContext(label: String, help: String?, icon: ImageWithOptions?, feedback: ObservableProperty<Pair<Importance, String>?>, field: VIEW): VIEW = defaultEntryContext(label, help, icon, feedback, field)

}

