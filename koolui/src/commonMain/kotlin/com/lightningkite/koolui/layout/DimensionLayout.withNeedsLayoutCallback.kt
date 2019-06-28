package com.lightningkite.koolui.layout

//Creation order: children(); view(); Layout(DimensionLayout(view.x, children.x), DimensionLayout(view.y, children.y))

/*

- Invalidate - goes up the tree marking invalidated, layout called by user on 'frame'
- layout - called with a 'start' and 'end', placing itself and its children

PROCESS

- 'invalidations' are made, bubbling up / everything starts as invalidated
- next frame 'Layout.layout' is called
- 'Layout.layout' cycles calling 'X.layout' and 'Y.layout' until there are no invalidations left OR it's been 3+ iterations
- 'layout' goes down through the tree, skipping wherever it hasn't been invalidated AND size is the same

PARTS

- 'DimensionMeasure' - Measures a view
- 'DimensionLayout' - Lays out a view
- 'Layout' - Combo of both X and Y measures/layouts, lifecycles
- 'ViewAdapter' - basically as is

MINIMAL WORK

- View claims it needs relayout VS View claims it has new size
- New size will lead to parent needing to relayout *IF* the size of said view actually can change
- Invalidation must climb tree because root controls it; however, on the way back down, we only want to hit what's necessary
- Child needs layout VS *I* need layout

*/

fun DimensionLayout.withNeedsLayoutCallback(callback: ()->Unit) = object : DimensionLayout by this {
    override fun childNeedsLayout(fromChild: DimensionLayout?) {
        this@withNeedsLayoutCallback.childNeedsLayout(fromChild)
        if(this.childNeedsLayout){
            callback()
        }
    }
}

