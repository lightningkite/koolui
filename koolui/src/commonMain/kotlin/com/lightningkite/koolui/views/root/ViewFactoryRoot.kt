package com.lightningkite.koolui.views.root

interface ViewFactoryRoot<VIEW> {

    /**
     * Wraps the given view in another view, if necessary for this view factory to function.
     * Some view factories need a certain root view to be accessible to add dialogs and such.
     */
    fun contentRoot(view: VIEW): VIEW = view
}