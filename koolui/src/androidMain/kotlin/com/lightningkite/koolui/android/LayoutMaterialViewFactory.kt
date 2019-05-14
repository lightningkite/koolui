package com.lightningkite.koolui.android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.webkit.WebView
import android.widget.*
import com.lightningkite.koolui.android.access.ActivityAccess
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.builders.align
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.*
import com.lightningkite.koolui.image.*
import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import com.lightningkite.koolui.implementationhelpers.defaultEntryContext
import com.lightningkite.koolui.implementationhelpers.defaultSmallWindow
import com.lightningkite.koolui.layout.*
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.lokalize.time.*
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.Locale
import com.lightningkite.reacktive.list.*
import com.lightningkite.reacktive.list.lifecycle.bind
import com.lightningkite.reacktive.property.*
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.lifecycle.listen
import com.lightningkite.recktangle.Point
import com.lightningkite.recktangle.Rectangle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

open class LayoutMaterialViewFactory(
        val access: ActivityAccess,
        override val theme: Theme,
        override val colorSet: ColorSet = theme.main
) : LayoutViewFactory<View>() {

    companion object {
        val NOTSET = -1.1f
    }

    val context = access.context

    val frameId = 0x00EFFFFF

    init {
        dip = context.resources.displayMetrics.density
    }

    override fun withColorSet(colorSet: ColorSet) =
            LayoutMaterialViewFactory(access = access, theme = theme, colorSet = colorSet)

    class LayoutView(context: Context) : ViewGroup(context) {
        var layout: Layout<*, View>? = null
        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {}
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun defaultView(): View = View(context)

    override fun <SPECIFIC : View> SPECIFIC.adapter(): ViewAdapter<SPECIFIC, View> = adapter(true)
    fun <SPECIFIC : View> SPECIFIC.adapter(addView: Boolean): ViewAdapter<SPECIFIC, View> = object : ViewAdapter<SPECIFIC, View> {
        override val view: SPECIFIC = this@adapter
        override val viewAsBase: View = this@adapter

        val rect = Rectangle(
                NOTSET,
                NOTSET,
                NOTSET,
                NOTSET
        )

        override fun updatePlacementX(start: Float, end: Float) {
            rect.left = start
            rect.right = end
            if (rect.top != NOTSET && rect.bottom != NOTSET) {
                layout(
                        (rect.left * dip).toInt(),
                        (rect.top * dip).toInt(),
                        (rect.right * dip).toInt(),
                        (rect.bottom * dip).toInt()
                )
            }
        }

        override fun updatePlacementY(start: Float, end: Float) {
            rect.top = start
            rect.bottom = end
            if (rect.left != NOTSET && rect.right != NOTSET) {
                layout(
                        (rect.left * dip).toInt(),
                        (rect.top * dip).toInt(),
                        (rect.right * dip).toInt(),
                        (rect.bottom * dip).toInt()
                )
            }
        }

        override fun onAddChild(layout: Layout<*, View>) {
            if (addView && view is ViewGroup) {
                view.addView(layout.viewAdapter.viewAsBase)
            }
        }

        override fun onRemoveChild(layout: Layout<*, View>) {
            if (addView && view is ViewGroup) {
                view.removeView(layout.viewAdapter.viewAsBase)
            }
        }
    }

    class IntrinsicSizeCalculators(val view: View) {

        var freeX: Boolean = false
        var freeY: Boolean = false

        inner class X() : BaseDimensionCalculator() {
            override fun measure(output: Measurement) {
                if (!freeX) {
                    freeY = true
                    view.measure(
                            View.MeasureSpec.makeMeasureSpec(10_000, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(10_000, View.MeasureSpec.UNSPECIFIED)
                    )
                }
                output.size = view.measuredWidth.toFloat()
            }

            override fun layoutChildren(size: Float) {}
        }

        inner class Y() : BaseDimensionCalculator() {
            override fun measure(output: Measurement) {
                if (!freeY) {
                    freeX = true
                    view.measure(
                            View.MeasureSpec.makeMeasureSpec(10_000, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(10_000, View.MeasureSpec.UNSPECIFIED)
                    )
                }
                output.size = view.measuredHeight.toFloat()
            }

            override fun layoutChildren(size: Float) {}
        }

        val x = X()
        val y = Y()
    }

    fun <SPECIFIC : View> SPECIFIC.intrinsicLayout(addView: Boolean = true): Layout<SPECIFIC, View> {
        val calc = IntrinsicSizeCalculators(this)
        return Layout<SPECIFIC, View>(
                viewAdapter = adapter(addView = addView),
                x = calc.x,
                y = calc.y
        )
    }


    override fun contentRoot(view: Layout<*, View>): Layout<*, View> {
        return super.contentRoot(view)
    }

    override fun entryContext(
            label: String,
            help: String?,
            icon: ImageWithSizing?,
            feedback: ObservableProperty<Pair<Importance, String>?>,
            field: Layout<*, View>
    ) = defaultEntryContext(label, help, icon, feedback, field)

    override fun <DEPENDENCY> window(
            dependency: DEPENDENCY,
            stack: MutableObservableList<ViewGenerator<DEPENDENCY, Layout<*, View>>>,
            tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, Layout<*, View>>>>
    ): Layout<*, View> = defaultSmallWindow(
            theme = theme,
            barBuilder = withColorSet(theme.bar),
            dependency = dependency,
            stack = stack,
            tabs = tabs
    )

    override fun <DEPENDENCY> pages(dependency: DEPENDENCY, page: MutableObservableProperty<Int>, vararg pageGenerator: ViewGenerator<DEPENDENCY, Layout<*, View>>): Layout<*, View> = align {
        AlignPair.FillFill + Layout(
                ViewPager(context).adapter(addView = false),
                LeafDimensionCalculator(0f, 100f, 0f),
                LeafDimensionCalculator(0f, 100f, 0f)
        ).apply {
            viewAdapter.view.apply {
                var iSet = false
                adapter = object : PagerAdapter() {
                    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

                    override fun getCount(): Int = pageGenerator.size

                    override fun instantiateItem(container: ViewGroup, position: Int): Any {
                        val layout = pageGenerator[position].generate(dependency)
                        container.addView(layout.viewAdapter.viewAsBase)
                        addChild(layout)
                        return layout
                    }

                    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                        val casted = `object` as Layout<*, View>
                        removeChild(casted)
                        container.removeView(casted.viewAdapter.viewAsBase)
                    }
                }
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
            }
        }
        AlignPair.BottomCenter + text(
                text = page.transform { "${it + 1} / ${pageGenerator.size}" },
                size = TextSize.Tiny
        )
    }

    override fun tabs(options: ObservableList<TabItem>, selected: MutableObservableProperty<TabItem>): Layout<*, View> = TabLayout(context).apply {
        var uiSet = false
        lifecycle.bind(options.onListUpdate) {
            removeAllTabs()
            for (item in it) {
                addTab(newTab().apply {
                    this.text = item.text
                    this.icon = item.imageWithSizing.android()
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
    }.intrinsicLayout()

    inner class ListViewHolder<T>(
            context: Context,
            val makeView: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> Layout<*, View>,
            val parent: TreeObservableProperty
    ) : RecyclerView.ViewHolder(LayoutView(context).apply {
        layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }) {
        var childLayout: Layout<*, View>? = null
        val parentLayout = Layout(
                viewAdapter = this.itemView.adapter(),
                x = FrameDimensionCalculator(child = { childLayout?.x ?: LeafDimensionCalculator(0f, 0f, 0f) }),
                y = FrameDimensionCalculator(child = { childLayout?.y ?: LeafDimensionCalculator(0f, 0f, 0f) })
        )
        var property: StandardObservableProperty<T>? = null
        var index: StandardObservableProperty<Int> = StandardObservableProperty(0)
        fun update(item: T, newIndex: Int) {
            GlobalScope.launch(Dispatchers.UI) {
                index.value = newIndex
                if (property == null) {
                    property = StandardObservableProperty(item)
                    val newView = makeView.invoke(property!!, index)
                    parentLayout.addChild(newView)
                    newView.invalidate()
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
    ): Layout<*, View> = RecyclerView(context).apply {
        layoutManager = LinearLayoutManager(
                context,
                if (direction.vertical) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL,
                !direction.uiPositive
        )
        adapter = object : RecyclerView.Adapter<ListViewHolder<T>>() {
            override fun getItemCount(): Int = data.size

            override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
            ): ListViewHolder<T> = ListViewHolder<T>(context, makeView, this@apply.lifecycle)

            override fun onBindViewHolder(holder: ListViewHolder<T>, position: Int) {
                holder.update(data[position], position)
            }
        }

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
                setByAndroid = true
                lastIndex.value = when (
                    val lm = recyclerView.layoutManager) {
                    is LinearLayoutManager -> lm.findLastVisibleItemPosition()
                    is GridLayoutManager -> lm.findLastVisibleItemPosition()
                    else -> 0
                }
            }
        })

        lifecycle.bind(firstIndex) {
            if (setByAndroid) {
                setByAndroid = false
                return@bind
            }
            scrollToPosition(it)
        }

        lifecycle.bind(lastIndex) {
            if (setByAndroid) {
                setByAndroid = false
                return@bind
            }
            scrollToPosition(it)
        }

        fun updateIndices() {
            for (index in 0 until childCount) {
                val holder = getChildViewHolder(getChildAt(index)) as? AndroidMaterialViewFactory.ListViewHolder<T>
                if (holder != null) {
                    holder.index.value = holder.adapterPosition
                }
            }
        }

        lifecycle.bind(data, ObservableListListenerSet<T>(
                onAddListener = { item, position -> adapter.notifyItemInserted(position); updateIndices() },
                onRemoveListener = { item, position -> adapter.notifyItemRemoved(position); updateIndices() },
                onChangeListener = { oldItem, newItem, position -> adapter.notifyItemChanged(position) },
                onMoveListener = { item, oldPosition, newPosition ->
                    adapter.notifyItemMoved(
                            oldPosition,
                            newPosition
                    )
                    updateIndices()
                },
                onReplaceListener = { list -> adapter.notifyDataSetChanged() }
        ))
    }.intrinsicLayout()

    override fun text(text: ObservableProperty<String>, importance: Importance, size: TextSize, align: AlignPair, maxLines: Int) = TextView(context).apply {
        textSize = size.sp()
        lifecycle.bind(text) {
            this.text = it
        }
        setTextColor(colorSet.importance(importance).toInt())
        gravity = align.android()
        setMaxLines(maxLines)
    }.intrinsicLayout()

    override fun image(imageWithSizing: ObservableProperty<ImageWithSizing>) = ImageView(context).apply {
        lifecycle.bind(imageWithSizing) {
            this.scaleType = when (it.scaleType) {
                ImageScaleType.Crop -> ImageView.ScaleType.CENTER_CROP
                ImageScaleType.Fill -> ImageView.ScaleType.FIT_CENTER
                ImageScaleType.Center -> ImageView.ScaleType.CENTER
            }
            val drawable = it.android()
            setImageDrawable(drawable)
            it.defaultSize?.let {
                minimumWidth = (it.x * dip).toInt()
                minimumHeight = (it.y * dip).toInt()
            }
        }
    }.intrinsicLayout()

    override fun web(content: ObservableProperty<String>) = WebView(context).apply {
        lifecycle.bind(content) {
            if (it.startsWith("http"))
                loadUrl(it)
            else
                loadData(it, "text/html", Charsets.UTF_8.toString())
        }
    }.intrinsicLayout()

    override fun space(size: Point) = Space(context).apply {
        minimumWidth = (size.x * dip).toInt()
        minimumHeight = (size.y * dip).toInt()
    }.intrinsicLayout()

    override fun button(label: ObservableProperty<String>, imageWithSizing: ObservableProperty<ImageWithSizing?>, importance: Importance, onClick: () -> Unit) = Button(context).apply {
        val colorSet = theme.importance(importance)
        if (importance == Importance.Low) {
            setBackgroundResource(selectableItemBackgroundResource)
        } else {
            background.setColorFilter(colorSet.background.toInt(), PorterDuff.Mode.MULTIPLY)
        }
        setTextColor(colorSet.foreground.toInt())
        lifecycle.bind(label) {
            this.text = it
        }
        lifecycle.bind(imageWithSizing) {
            setCompoundDrawablesWithIntrinsicBounds(it?.android(), null, null, null)
        }
        setOnClickListener { onClick.invoke() }
    }.intrinsicLayout()

    override fun imageButton(imageWithSizing: ObservableProperty<ImageWithSizing>, label: ObservableProperty<String?>, importance: Importance, onClick: () -> Unit) = when (importance) {
        Importance.Low -> imageButtonEmbedded(imageWithSizing, label, importance, onClick)
        Importance.Normal -> imageButtonFAB(imageWithSizing, label, importance, onClick)
        Importance.High -> imageButtonFAB(imageWithSizing, label, importance, onClick)
        Importance.Danger -> imageButtonFAB(imageWithSizing, label, importance, onClick)
    }.intrinsicLayout()


    override fun <T> picker(options: ObservableList<T>, selected: MutableObservableProperty<T>, toString: (T) -> String): Layout<Spinner, View> {
        val view = Spinner(context)
        val layout = view.intrinsicLayout()
        view.apply {
            val newAdapter: StandardListAdapter<T> = StandardListAdapter<T>(options, layout, toString)
            adapter = newAdapter

            var indexAlreadySet = false

            lifecycle.listen(options.onListUpdate) {
                newAdapter.notifyDataSetChanged()
                val index = options.indexOf(selected.value)
                //            println("update to $index - ${selected.value}")
                if (index == -1) {
                    //                println("could not find ${selected.value}")
                    setSelection(0)
                    return@listen
                }
                setSelection(index)
            }

            lifecycle.bind(selected) { it ->
                val index = options.indexOf(it)
                //            println("selected to $index - $it")
                if (index == -1) {
                    //                println("could not find ${it?.hashCode()} in ${options.joinToString { it?.hashCode().toString() }}")
                    setSelection(0)
                    return@bind
                }
                if (!indexAlreadySet) {
                    setSelection(index)
                } else {
                    indexAlreadySet = false
                }
            }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    println("set to $position - ${options[position]}")
                    indexAlreadySet = true
                    selected.value = (options[position])
                }
            }
        }
        return layout
    }

    override fun textField(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType) = EditText(context).apply {
        inputType = type.android()
        hint = placeholder
        setText(text.value)
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.value != s) {
                    text.value = (s.toString())
                }
            }
        })
        lifecycle.listen(text) {
            if (it != this@apply.text.toString()) {
                this.setText(it)
            }
        }

        setTextColor(colorSet.foreground.toInt())
        setHintTextColor(colorSet.foregroundDisabled.toInt())
    }.intrinsicLayout()

    override fun textArea(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType) = textField(text, placeholder, type).apply {
        this.viewAdapter.view.apply {
            gravity = Gravity.TOP or Gravity.START
            maxLines = Int.MAX_VALUE
            minHeight = (100 * dip).toInt()
        }
    }

    override fun numberField(value: MutableObservableProperty<Number?>, placeholder: String, type: NumberInputType, decimalPlaces: Int) = EditText(context).apply {
        inputType = type.android()
        hint = placeholder

        val format = if (decimalPlaces == 0) DecimalFormat("#") else DecimalFormat("#." + "#".repeat(decimalPlaces))

        infix fun Number?.basicallyDifferent(other: Number?): Boolean {
            if (this == null && other == null) return false
            if (this == null) return true
            if (other == null) return true
            return abs(this.toDouble() - other.toDouble()) > 0.00001
        }

        var lastValue: Double? = null
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastValue = null

                setTextColor(colorSet.foreground.toInt())
                if (!s.isNullOrBlank()) {
                    try {
                        lastValue = format.parse(s.toString()).toDouble()
                    } catch (e: ParseException) {
                        try {
                            lastValue = s.toString().toDouble()
                        } catch (e: NumberFormatException) {
                            //Not a number?
                            setTextColor(Color.red.toInt())
                        }
                    }
                }

                if (value.value basicallyDifferent lastValue) {
                    value.value = (lastValue)
                }
            }
        })

        lifecycle.bind(value) {
            if (it basicallyDifferent lastValue) {
                if (it == null) this.setText("")
                else this.setText(format.format(it))
            }
        }

        setHintTextColor(colorSet.foregroundDisabled.toInt())
    }.intrinsicLayout()

    override fun datePicker(observable: MutableObservableProperty<Date>) = button(
            label = observable.transform { Locale.default.renderDate(it) },
            onClick = {
                val start: Calendar = observable.value.toJava()
                DatePickerDialog(
                        context,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            start.set(Calendar.YEAR, year)
                            start.set(Calendar.MONTH, monthOfYear)
                            start.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            observable.value = start.toDate()
                        },
                        start.get(Calendar.YEAR),
                        start.get(Calendar.MONTH),
                        start.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
    )

    override fun dateTimePicker(observable: MutableObservableProperty<DateTime>) = button(
            label = observable.transform { Locale.default.renderDateTime(it) },
            onClick = {
                val start: Calendar = observable.value.toJava()
                DatePickerDialog(
                        context,
                        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                            start.set(Calendar.YEAR, year)
                            start.set(Calendar.MONTH, monthOfYear)
                            start.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            TimePickerDialog(
                                    context,
                                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                                        start.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        start.set(Calendar.MINUTE, minute)
                                        observable.value = start.toDateTime()
                                    },
                                    start.get(Calendar.HOUR_OF_DAY),
                                    start.get(Calendar.MINUTE),
                                    false
                            ).show()
                        },
                        start.get(Calendar.YEAR),
                        start.get(Calendar.MONTH),
                        start.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
    )

    override fun timePicker(observable: MutableObservableProperty<Time>): Layout<*, View> = button(
            label = observable.transform { Locale.default.renderTime(it) },
            onClick = {
                val start: Calendar = observable.value.toJava()
                TimePickerDialog(
                        context,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            start.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            start.set(Calendar.MINUTE, minute)
                            observable.value = start.toTime()
                        },
                        start.get(Calendar.HOUR_OF_DAY),
                        start.get(Calendar.MINUTE),
                        false
                ).show()
            }
    )

    override fun slider(range: IntRange, observable: MutableObservableProperty<Int>): Layout<*, View> = SeekBar(context).apply {
        max = range.endInclusive - range.start + 1
        lifecycle.bind(observable) {
            val newProg = it - range.start
            if (this.progress != newProg) {
                this.progress = newProg
            }
        }
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newValue = progress + range.start
                    if (observable.value != newValue) {
                        observable.value = newValue
                    }
                }
            }
        })
    }.intrinsicLayout()

    override fun toggle(observable: MutableObservableProperty<Boolean>) = CheckBox(context).apply {
        this.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked != observable.value) {
                observable.value = (isChecked)
            }
        }
        lifecycle.bind(observable) {
            val value = observable.value
            if (isChecked != value) {
                isChecked = value
            }
        }
    }.intrinsicLayout()

    override fun refresh(contains: Layout<*, View>, working: ObservableProperty<Boolean>, onRefresh: () -> Unit): Layout<SwipeRefreshLayout, View> {
        val view = SwipeRefreshLayout(context)
        val layout = view.intrinsicLayout()
        view.apply {
            layout.addChild(contains)
            contains.viewAdapter.viewAsBase.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            lifecycle.bind(working) {
                this.isRefreshing = it
            }
            this.setOnRefreshListener {
                onRefresh()
            }
        }
        return layout
    }

    override fun work(view: Layout<*, View>, isWorking: ObservableProperty<Boolean>): Layout<*, View> {
        val bar = ProgressBar(context).apply {
            isIndeterminate = true
        }.intrinsicLayout()
        return swap(
                view = isWorking.transform {
                    val nextView = if (it) bar else view
                    nextView to Animation.Fade
                }
        )
    }

    override fun progress(view: Layout<*, View>, progress: ObservableProperty<Float>): Layout<*, View> {
        val bar = ProgressBar(context).apply {
            isIndeterminate = false
            max = 100
            lifecycle.bind(progress) {
                this.progress = (it * 100).toInt()
            }
        }.intrinsicLayout()
        return swap(
                view = progress.transform {
                    val nextView = if (it == 1f) view else bar
                    nextView to Animation.Fade
                }
        )
    }

    override fun scrollBoth(view: Layout<*, View>, amountX: MutableObservableProperty<Float>, amountY: MutableObservableProperty<Float>) = scrollVertical(scrollHorizontal(view, amountX), amountY)

    override fun scrollVertical(view: Layout<*, View>, amount: MutableObservableProperty<Float>): Layout<*, View> {
        val scrollView = ScrollView(context)
        val layout = scrollView.intrinsicLayout()
        scrollView.apply {
            isFillViewport = true
            view.viewAdapter.viewAsBase.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            layout.addChild(view)

            var suppressListener = false
            lifecycle.bind(amount) {
                suppressListener = true
                scrollX = it.roundToInt()
            }
            viewTreeObserver.addOnScrollChangedListener {
                if (suppressListener) {
                    suppressListener = false
                    return@addOnScrollChangedListener
                }
                amount.value = scrollX.toFloat()
            }
        }
        return layout
    }

    override fun scrollHorizontal(view: Layout<*, View>, amount: MutableObservableProperty<Float>): Layout<*, View> {
        val scrollView = HorizontalScrollView(context)
        val layout = scrollView.intrinsicLayout()
        scrollView.apply {
            isFillViewport = true
            view.viewAdapter.viewAsBase.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            layout.addChild(view)

            var suppressListener = false
            lifecycle.bind(amount) {
                suppressListener = true
                scrollY = it.roundToInt()
            }
            viewTreeObserver.addOnScrollChangedListener {
                if (suppressListener) {
                    suppressListener = false
                    return@addOnScrollChangedListener
                }
                amount.value = scrollY.toFloat()
            }
        }
        return layout
    }

    override fun card(view: Layout<*, View>): Layout<*, View> {
        val cardView = CardView(context)
        val layout = cardView.intrinsicLayout()
        view.viewAdapter.viewAsBase.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        layout.addChild(view)
        cardView.setCardBackgroundColor(colorSet.backgroundHighlighted.toInt())
        return layout
    }

    override fun Layout<*, View>.background(color: ObservableProperty<Color>): Layout<*, View> {
        isAttached.bind(color) {
            viewAdapter.viewAsBase.setBackgroundColor(it.toInt())
        }
        return this
    }

    override fun Layout<*, View>.alpha(alpha: ObservableProperty<Float>): Layout<*, View> {
        isAttached.bind(alpha) {
            viewAdapter.viewAsBase.alpha = it
        }
        return this
    }

    override fun Layout<*, View>.clickable(onClick: () -> Unit): Layout<*, View> {
        viewAdapter.viewAsBase.setOnClickListener { onClick() }
        return this
    }

    override fun Layout<*, View>.altClickable(onAltClick: () -> Unit): Layout<*, View> {
        viewAdapter.viewAsBase.setOnLongClickListener { onAltClick(); true }
        return this
    }

    override fun launchDialog(
            dismissable: Boolean,
            onDismiss: () -> Unit,
            makeView: (dismissDialog: () -> Unit) -> Layout<*, View>
    ) {
        TODO()
//        val frame = access.activity?.findViewById<FrameLayout>(frameId) ?: return
//        var dismisser: () -> Unit = {}
//        val generatedView = makeView { dismisser() }
//        val wrapper = align(
//                AlignPair.CenterCenter to generatedView.apply {
//                    if (!hasOnClickListeners()) {
//                        setOnClickListener { /*squish*/ }
//                    }
//                }
//        )
//                .clickable { dismisser() }
//                .apply {
//                    setBackgroundColor(Color.black.copy(alpha = .5f).toInt())
//                    alpha = 0f
//                    setPadding(
//                            (16 * dip).toInt(),
//                            (16 * dip).toInt(),
//                            (16 * dip).toInt(),
//                            (16 * dip).toInt()
//                    )
//                }
//        frame.addView(wrapper, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
//        wrapper.animate().alpha(1f).setDuration(250).start()
//        wrapper.lifecycle.parent = frame.lifecycle
//        dismisser = {
//            wrapper.animate().alpha(0f).setDuration(250).withEndAction {
//                frame.removeView(wrapper)
//                onDismiss()
//            }.start()
//        }
    }

    override fun launchSelector(
            title: String?,
            options: List<Pair<String, () -> Unit>>
    ) {
        AlertDialog.Builder(access.context)
                .setTitle(title)
                .setItems(
                        options.map { it.first }.toTypedArray()
                ) { _, option ->
                    options[option].second.invoke()
                }
                .show()
    }


    fun imageButtonEmbedded(
            imageWithSizing: ObservableProperty<ImageWithSizing>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ): View = ImageButton(context).apply {
        val padding = (dip * 8).toInt()
        this.setPadding(padding, padding, padding, padding)
        lifecycle.bind(label) {
            if (Build.VERSION.SDK_INT > 26) {
                this.tooltipText = it
            }
        }
        lifecycle.bind(imageWithSizing) {
            setBackgroundResource(selectableItemBackgroundResource)
            setImageDrawable(it.android())
        }
        setOnClickListener { onClick.invoke() }
    }

    fun imageButtonRect(
            imageWithSizing: ObservableProperty<ImageWithSizing>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ): View = ImageButton(context).apply {
        lifecycle.bind(label) {
            if (Build.VERSION.SDK_INT > 26) {
                this.tooltipText = it
            }
        }
        lifecycle.bind(imageWithSizing) {
            if (importance == Importance.Low) {
                setBackgroundResource(selectableItemBackgroundResource)
                setImageDrawable(it.android())
            } else {
                val drawable = it.android()
                background = ShapeDrawable(RoundRectShape(FloatArray(8) { _ -> 4 * dip }, null, null)).apply {
                    this.paint.color = theme.importance(importance).background.toInt()
                }.let { circle ->
                    if (Build.VERSION.SDK_INT >= 21) {
                        RippleDrawable(
                                theme.importance(importance).androidForegroundOverlay(),
                                circle,
                                circle
                        )
                    } else {
                        circle
                    }
                }
                setImageDrawable(drawable)
            }
        }
        setOnClickListener { onClick.invoke() }
    }

    fun imageButtonFAB(
            imageWithSizing: ObservableProperty<ImageWithSizing>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ): View = FloatingActionButton(context).apply {
        backgroundTintList = theme.importance(importance).androidBackground()
        rippleColor = theme.importance(importance).backgroundHighlighted.toInt()
        lifecycle.bind(label) {
            if (Build.VERSION.SDK_INT > 26) {
                this.tooltipText = it
            }
        }
        lifecycle.bind(imageWithSizing) {
            setImageDrawable(it.android())
        }
        setOnClickListener { onClick.invoke() }
    }

    inner class StandardListAdapter<T>(
            list: List<T>,
            val parent: Layout<*, View>,
            val toString: (T) -> String
    ) : BaseAdapter() {

        inner class ItemObservable(init: T) : StandardObservableProperty<T>(init) {
            var index: Int = 0
            override fun remove(element: (T) -> Unit): Boolean {
                return super.remove(element)
            }
        }

        var list: List<T> = list
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getCount(): Int = list.size
        override fun getItem(position: Int): Any? = list[position]
        override fun getItemId(position: Int): Long = position.toLong()

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            return if (convertView == null) {
                val newObs = ItemObservable(list[position])
                val newLayout = text(
                        text = newObs.transform(toString)
                )
                this.parent.addChild(newLayout)
                val newView = newLayout.viewAdapter.view
                newView.tag = newObs
                newObs.index = position
                newView.layoutParams = AbsListView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                newView
            } else {
                val obs = convertView.tag as StandardListAdapter<T>.ItemObservable
                obs.index = position
                obs.value = (list[position])
                convertView
            }
        }
    }
}
