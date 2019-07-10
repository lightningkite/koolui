package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.layout.Layout
import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.lifecycle.listen
import com.lightningkite.recktangle.Rectangle
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.Foundation.NSIndexPath
import platform.UIKit.*
import platform.darwin.NSInteger
import platform.darwin.NSObject
import platform.darwin.sel_registerName

class ListDataSource<T>(
        val data: List<T>,
        val makeLayout: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> Layout<*, UIView>
) : NSObject(), UITableViewDataSourceProtocol {
    val cellId = "cell"
    val holdingValueObsId = "valueObs"
    val holdingIndexObsId = "indexObs"
    override fun tableView(tableView: UITableView, cellForRowAtIndexPath: NSIndexPath): UITableViewCell {
        val index = cellForRowAtIndexPath.row.toInt()
        val cell = tableView.dequeueReusableCellWithIdentifier(cellId) as? LayoutCellView
                ?: LayoutCellView(CGRect.zeroVal).apply {
                    val valueObs = StandardObservableProperty<T>(data[index])
                    val indexObs = StandardObservableProperty(index)
                    val layout = makeLayout(valueObs, indexObs)
                    (layout.viewAdapter as UIViewAdapter<*>).holding[holdingValueObsId] = valueObs
                    (layout.viewAdapter as UIViewAdapter<*>).holding[holdingIndexObsId] = indexObs
                    setup(layout)
                }
        val valueObs = (cell.layout.viewAdapter as UIViewAdapter<*>).holding[holdingValueObsId] as MutableObservableProperty<T>
        val indexObs = (cell.layout.viewAdapter as UIViewAdapter<*>).holding[holdingIndexObsId] as MutableObservableProperty<Int>
        valueObs.value = data[index]
        indexObs.value = index
        return cell
    }

    override fun tableView(tableView: UITableView, numberOfRowsInSection: NSInteger): NSInteger {
        return data.size.toLong()
    }
}

class LayoutCellView : UITableViewCell {
    @OverrideInit
    constructor(frame: CValue<CGRect>) : super(frame)

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    lateinit var layout: Layout<*, UIView>

    fun setup(layout: Layout<*, UIView>) {
        this.layout = layout
        layout.isAttached.alwaysOn = true

        addSubview(layout.viewAsBase)
    }

    val rect = Rectangle()

    @ObjCAction
    fun layoutSubviews() {
        frame.useContents {
            rect.left = (origin.x).toFloat()
            rect.top = (origin.y).toFloat()
            rect.right = (origin.x + size.width).toFloat()
            rect.bottom = (origin.y + size.height).toFloat()
        }
        println("Laying out subviews in rect $rect")
        layout.layout(rect)
    }

    init {
        sel_registerName("layoutSubviews")
    }
}
