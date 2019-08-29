package com.lightningkite.koolui.view.navigation

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.lightningkite.kommon.collection.pop
import com.lightningkite.koolui.android.LayoutToAndroidView
import com.lightningkite.koolui.android.android
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.TabItem
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.LeafDimensionLayout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.view.HasActivityAccess
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.koolui.views.layout.align
import com.lightningkite.koolui.views.navigation.ViewFactoryNavigationDefault
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.ObservableListListenerSet
import com.lightningkite.reacktive.list.lifecycle.bind
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.lifecycle.listen
import com.lightningkite.reacktive.property.transform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface LayoutAndroidNavigation : ViewFactoryNavigationDefault<Layout<*, View>>, LayoutViewWrapper<View>, HasActivityAccess {

    override fun <DEPENDENCY> window(dependency: DEPENDENCY, stack: MutableObservableList<ViewGenerator<DEPENDENCY, Layout<*, View>>>, tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, Layout<*, View>>>>): Layout<*, View> {
        return super.window(dependency, stack, tabs).apply {
            isAttached.listen(activityAccess.onBackPressed) {
                if(stack.size > 1) {
                    stack.pop()
                    true
                }  else {
                    false
                }
            }
        }
    }

    override fun <DEPENDENCY> pages(
            dependency: DEPENDENCY,
            page: MutableObservableProperty<Int>,
            vararg pageGenerator: ViewGenerator<DEPENDENCY, Layout<*, View>>
    ): Layout<*, View> = align {
        AlignPair.FillFill + Layout(
                ViewPager(context).adapter(),
                LeafDimensionLayout(0f, 100f, 0f),
                LeafDimensionLayout(0f, 100f, 0f)
        ).apply {
            val newAdapter = object : PagerAdapter() {
                override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

                override fun getCount(): Int = pageGenerator.size

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    val layout = pageGenerator[position].generate(dependency)
                    val newView = LayoutToAndroidView(context).also { it.layout = layout }
                    addSemiChild(layout)
                    newView.setBackgroundColor(Color.blue.copy(alpha = .4f).toInt())
                    newView.layoutParams = ViewPager.LayoutParams().apply {
                        width = ViewGroup.LayoutParams.MATCH_PARENT
                        height = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                    container.addView(newView)
//                    val newView = View(context).apply {
//                        setBackgroundColor(Color.blue.copy(alpha = .4f).toInt())
//                        minimumHeight = 100
//                        minimumWidth = 100
//                    }
//                    newView.layoutParams = ViewPager.LayoutParams().apply {
//                        width = MATCH_PARENT
//                        height = MATCH_PARENT
//                    }
//                    container.addView(newView)
                    return newView
                }

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    val casted = `object` as LayoutToAndroidView
                    casted.isAttached.parent = null
                    container.removeView(casted)
                }
            }
            viewAdapter.view.apply {
                setBackgroundColor(Color.green.copy(alpha = .4f).toInt())
                adapter = newAdapter
                var iSet = false
                this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) {
                        iSet = true
                        page.value = position
                    }
                })
                isAttached.bind(page) {
                    if (iSet) {
                        iSet = false
                        return@bind
                    }
                    this.setCurrentItem(it, false)
                }
//                post {
                newAdapter.notifyDataSetChanged()
//                }
            }
        }
        AlignPair.BottomCenter + text(
                text = page.transform { "${it + 1} / ${pageGenerator.size}" },
                size = TextSize.Tiny
        )
    }

    override fun tabs(options: ObservableList<TabItem>, selected: MutableObservableProperty<TabItem>): Layout<*, View> = intrinsicLayout(TabLayout(context)) { layout ->
        var uiSet = false
        layout.isAttached.bind(options.onListUpdate) {
            removeAllTabs()
            for (item in it) {
                addTab(newTab().apply {
                    this.text = item.text
                    this.icon = item.imageWithOptions.android()
                    this.tag = item
                    if (selected.value == item) {
                        uiSet = true
                        this.select()
                    }
                })
            }
        }
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (uiSet) {
                    uiSet = false
                    return
                }
                (tab?.tag as? TabItem)?.let { selected.value = it }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (uiSet) {
                    uiSet = false
                    return
                }
                (tab?.tag as? TabItem)?.let { selected.value = it }
            }
        })
    }

    class ListViewHolder<T>(
            context: Context,
            val direction: Direction,
            val makeView: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> Layout<*, View>,
            val parent: TreeObservableProperty,
            val frame: LayoutToAndroidView = LayoutToAndroidView(context)
    ) : RecyclerView.ViewHolder(frame) {
        init {
            frame.layoutParams = if (direction.vertical) {
                RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            } else {
                RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            frame.attachParent(parent)
        }

        var property: StandardObservableProperty<T>? = null
        var index: StandardObservableProperty<Int> = StandardObservableProperty(0)
        fun update(item: T, newIndex: Int) {
            GlobalScope.launch(Dispatchers.UI) {
                index.value = newIndex
                if (property == null) {
                    property = StandardObservableProperty(item)
                    val newView = makeView.invoke(property!!, index)
                    frame.layout = newView
                } else {
                    property!!.value = item
                }
            }
        }
    }

    override fun <T> list(
            data: ObservableList<T>,
            firstIndex: MutableObservableProperty<Int>,
            lastIndex: MutableObservableProperty<Int>,
            direction: Direction,
            makeView: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> Layout<*, View>
    ): Layout<*, View> = intrinsicLayout(RecyclerView(context)) { layout ->

        layoutManager = LinearLayoutManager(
                context,
                if (direction.vertical) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL,
                !direction.uiPositive
        )
        val newAdapter = object : RecyclerView.Adapter<ListViewHolder<T>>() {
            override fun getItemCount(): Int = data.size

            override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
            ): ListViewHolder<T> = ListViewHolder<T>(context, direction, makeView, layout.isAttached)

            override fun onBindViewHolder(holder: ListViewHolder<T>, position: Int) {
                holder.update(data[position], position)
            }
        }
        adapter = newAdapter

        var setByAndroid = false
        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                setByAndroid = true
                firstIndex.value = when (
                    val lm = recyclerView.layoutManager) {
                    is LinearLayoutManager -> lm.findFirstVisibleItemPosition()
                    is GridLayoutManager -> lm.findFirstVisibleItemPosition()
                    else -> 0
                }
                lastIndex.value = when (
                    val lm = recyclerView.layoutManager) {
                    is LinearLayoutManager -> lm.findLastVisibleItemPosition()
                    is GridLayoutManager -> lm.findLastVisibleItemPosition()
                    else -> 0
                }
                setByAndroid = false
            }
        })

        layout.isAttached.bind(firstIndex) {
            if (setByAndroid) {
                return@bind
            }
            scrollToPosition(it)
        }

        layout.isAttached.listen(lastIndex) {
            if (setByAndroid) {
                return@listen
            }
            scrollToPosition(it)
        }

        fun updateIndices() {
            for (index in 0 until childCount) {
                @Suppress("UNCHECKED_CAST") val holder = getChildViewHolder(getChildAt(index)) as? ListViewHolder<T>
                if (holder != null) {
                    holder.index.value = holder.adapterPosition
                }
            }
        }

        layout.isAttached.bind(data, ObservableListListenerSet<T>(
                onAddListener = { item, position -> newAdapter.notifyItemInserted(position); updateIndices() },
                onRemoveListener = { item, position -> newAdapter.notifyItemRemoved(position); updateIndices() },
                onChangeListener = { oldItem, newItem, position -> newAdapter.notifyItemChanged(position) },
                onMoveListener = { item, oldPosition, newPosition ->
                    newAdapter.notifyItemMoved(
                            oldPosition,
                            newPosition
                    )
                    updateIndices()
                },
                onReplaceListener = { list -> newAdapter.notifyDataSetChanged() }
        ))
    }
}