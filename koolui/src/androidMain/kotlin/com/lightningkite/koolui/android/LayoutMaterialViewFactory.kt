package com.lightningkite.koolui.android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.PagerAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.lightningkite.koolui.canvas.AndroidCanvas
import com.lightningkite.koolui.canvas.Canvas
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
import com.lightningkite.koolui.views.Touch
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

open class LayoutMaterialViewFactory(
        val access: ActivityAccess,
        override val theme: Theme,
        override val colorSet: ColorSet = theme.main,
        root: Layout<*, View>? = null
) : LayoutViewFactory<View>(root) {
    override fun withColorSet(colorSet: ColorSet) =
            LayoutMaterialViewFactory(access = access, theme = theme, colorSet = colorSet, root = root)

    override fun canvas(draw: ObservableProperty<Canvas.() -> Unit>): Layout<*, View> = intrinsicLayout(CanvasView(access.context)){ layout ->
        val c = AndroidCanvas()
        layout.isAttached.bind(draw){
            this.render = { c.canvas = this; c.it() }
        }
    }

    val context = access.context

    val frameId = 0x00EFFFFF

    init {
        dip = context.resources.displayMetrics.density
    }


    override fun applyEntranceTransition(view: View, animation: Animation) {
        val parent = view.parent as? ViewGroup ?: return
        animation.android().animateIn.invoke(view, parent)
    }

    override fun applyExitTransition(view: View, animation: Animation, onComplete: () -> Unit) {
        val parent = view.parent as? ViewGroup ?: run {
            onComplete()
            return
        }
        animation.android().animateOut.invoke(view, parent).withEndAction(onComplete)
    }

    override fun defaultViewContainer(): View = ManualLayout(context)

    override fun <SPECIFIC : View> SPECIFIC.adapter() = adapter(true)
    fun <SPECIFIC : View> SPECIFIC.adapter(addView: Boolean) = AndroidLayoutAdapter(this, addView)

    fun <SPECIFIC : View> intrinsicLayout(
            view: SPECIFIC,
            addView: Boolean = true,
            setup: SPECIFIC.(layout: Layout<SPECIFIC, View>) -> Unit
    ): Layout<SPECIFIC, View> {
        val adapter = view.adapter(addView = addView)
        val calc = IntrinsicDimensionLayouts(view)
        val result = Layout(
                viewAdapter = adapter,
                x = calc.x,
                y = calc.y
        )
        setup(view, result)
        return result
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
                LeafDimensionLayout(0f, 100f, 0f),
                LeafDimensionLayout(0f, 100f, 0f)
        ).apply {
            val newAdapter = object : PagerAdapter() {
                override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

                override fun getCount(): Int = pageGenerator.size

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    val layout = pageGenerator[position].generate(dependency)
                    val newView = LayoutToAndroidView(context).also { it.layout = layout }
                    newView.setBackgroundColor(Color.blue.copy(alpha = .4f).toInt())
                    newView.attachParent(isAttached)
                    newView.layoutParams = ViewPager.LayoutParams().apply {
                        width = MATCH_PARENT
                        height = MATCH_PARENT
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
    }

    inner class ListViewHolder<T>(
            context: Context,
            val direction: Direction,
            val makeView: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> Layout<*, View>,
            val parent: TreeObservableProperty,
            val frame: LayoutToAndroidView = LayoutToAndroidView(context)
    ) : RecyclerView.ViewHolder(frame) {
        init {
            frame.layoutParams = if (direction.vertical) {
                RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            } else {
                RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
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
                val holder = getChildViewHolder(getChildAt(index)) as? AndroidMaterialViewFactory.ListViewHolder<T>
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

    override fun text(
            text: ObservableProperty<String>,
            importance: Importance,
            size: TextSize,
            align: AlignPair,
            maxLines: Int
    ) = intrinsicLayout(TextView(context)) { layout ->
        textSize = size.sp()
        layout.isAttached.bind(text) {
            this.text = it
            layout.requestMeasurement()
        }
        setTextColor(colorSet.importance(importance).toInt())
        gravity = align.android()
        setMaxLines(maxLines)
    }

    override fun image(imageWithSizing: ObservableProperty<ImageWithSizing>) = intrinsicLayout(ImageView(context)) { layout ->
        layout.isAttached.bind(imageWithSizing) {
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
            layout.requestMeasurement()
        }
    }

    override fun web(content: ObservableProperty<String>) = intrinsicLayout(WebView(context)) { layout ->
        layout.isAttached.bind(content) {
            if (it.startsWith("http"))
                loadUrl(it)
            else
                loadData(it, "text/html", Charsets.UTF_8.toString())
        }
    }

    override fun space(size: Point) = intrinsicLayout(View(context)) { layout ->
        minimumWidth = (size.x * dip).toInt()
        minimumHeight = (size.y * dip).toInt()
    }

    override fun button(
            label: ObservableProperty<String>,
            imageWithSizing: ObservableProperty<ImageWithSizing?>,
            importance: Importance,
            onClick: () -> Unit
    ) = intrinsicLayout(Button(context)) { layout ->
        val colorSet = theme.importance(importance)
        if (importance == Importance.Low) {
            setBackgroundResource(selectableItemBackgroundResource)
        } else {
            background.setColorFilter(colorSet.background.toInt(), PorterDuff.Mode.MULTIPLY)
        }
        setTextColor(colorSet.foreground.toInt())
        layout.isAttached.bind(label) {
            this.text = it
            layout.requestMeasurement()
        }
        layout.isAttached.bind(imageWithSizing) {
            setCompoundDrawablesWithIntrinsicBounds(it?.android(), null, null, null)
            layout.requestMeasurement()
        }
        setOnClickListener { onClick.invoke() }
    }

    override fun imageButton(
            imageWithSizing: ObservableProperty<ImageWithSizing>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ) = when (importance) {
        Importance.Low -> imageButtonEmbedded(imageWithSizing, label, importance, onClick)
        Importance.Normal -> imageButtonFAB(imageWithSizing, label, importance, onClick)
        Importance.High -> imageButtonFAB(imageWithSizing, label, importance, onClick)
        Importance.Danger -> imageButtonFAB(imageWithSizing, label, importance, onClick)
    }


    override fun <T> picker(
            options: ObservableList<T>,
            selected: MutableObservableProperty<T>,
            toString: (T) -> String
    ): Layout<Spinner, View> = intrinsicLayout(Spinner(context)) { layout ->
        val newAdapter: StandardListAdapter<T> = StandardListAdapter<T>(options, layout.isAttached, toString)
        adapter = newAdapter

        var indexAlreadySet = false

        layout.isAttached.listen(options.onListUpdate) {
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

        layout.isAttached.bind(selected) { it ->
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
            layout.requestMeasurement()
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

    override fun textField(
            text: MutableObservableProperty<String>,
            placeholder: String,
            type: TextInputType
    ) = intrinsicLayout(EditText(context)) { layout ->
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
        layout.isAttached.listen(text) {
            if (it != this@intrinsicLayout.text.toString()) {
                this.setText(it)
            }
        }

        setTextColor(colorSet.foreground.toInt())
        setHintTextColor(colorSet.foregroundDisabled.toInt())
    }

    override fun textArea(
            text: MutableObservableProperty<String>,
            placeholder: String,
            type: TextInputType
    ) = textField(text, placeholder, type).apply {
        this.viewAdapter.view.apply {
            gravity = Gravity.TOP or Gravity.START
            maxLines = Int.MAX_VALUE
            minHeight = (100 * dip).toInt()
        }
    }

    override fun numberField(
            value: MutableObservableProperty<Double?>,
            placeholder: String,
            allowNegatives: Boolean,
            decimalPlaces: Int
    ) = intrinsicLayout(EditText(context)) { layout ->
        hint = placeholder

        val format = if (decimalPlaces == 0) DecimalFormat("#") else DecimalFormat("#." + "#".repeat(decimalPlaces))

        infix fun Double?.basicallyDifferent(other: Double?): Boolean {
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

        layout.isAttached.bind(value) {
            if (it basicallyDifferent lastValue) {
                if (it == null) this.setText("")
                else this.setText(format.format(it))
            }
        }

        setHintTextColor(colorSet.foregroundDisabled.toInt())
    }

    override fun integerField(value: MutableObservableProperty<Long?>, placeholder: String, allowNegatives: Boolean) = intrinsicLayout(EditText(context)) { layout ->
        hint = placeholder

        val format = DecimalFormat("#")

        var lastValue: Long? = null
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastValue = null

                setTextColor(colorSet.foreground.toInt())
                if (!s.isNullOrBlank()) {
                    try {
                        lastValue = format.parse(s.toString()).toLong()
                    } catch (e: ParseException) {
                        try {
                            lastValue = s.toString().toLong()
                        } catch (e: NumberFormatException) {
                            //Not a number?
                            setTextColor(Color.red.toInt())
                        }
                    }
                }

                if (value.value == lastValue) {
                    value.value = (lastValue)
                }
            }
        })

        layout.isAttached.bind(value) {
            if (it == lastValue) {
                if (it == null) this.setText("")
                else this.setText(format.format(it))
            }
        }

        setHintTextColor(colorSet.foregroundDisabled.toInt())
    }

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
                            observable.value = start.toDate() as Date
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
                            observable.value = start.toTime() as Time
                        },
                        start.get(Calendar.HOUR_OF_DAY),
                        start.get(Calendar.MINUTE),
                        false
                ).show()
            }
    )

    override fun slider(range: IntRange, observable: MutableObservableProperty<Int>): Layout<*, View> = intrinsicLayout(SeekBar(context)) { layout ->
        max = range.endInclusive - range.start + 1
        layout.isAttached.bind(observable) {
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
    }

    override fun toggle(observable: MutableObservableProperty<Boolean>) = intrinsicLayout(CheckBox(context)) { layout ->
        this.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked != observable.value) {
                observable.value = (isChecked)
            }
        }
        layout.isAttached.bind(observable) {
            val value = observable.value
            if (isChecked != value) {
                isChecked = value
            }
        }
    }

    override fun refresh(
            contains: Layout<*, View>,
            working: ObservableProperty<Boolean>,
            onRefresh: () -> Unit
    ): Layout<SwipeRefreshLayout, View> {
        val view = SwipeRefreshLayout(context)
        val layout = Layout.frame(view.adapter(false), contains)
        view.addView(contains.viewAdapter.viewAsBase)
        contains.viewAdapter.viewAsBase.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        layout.isAttached.bind(working) {
            view.isRefreshing = it
        }
        view.setOnRefreshListener {
            onRefresh()
        }
        return layout
    }

    override fun work(): Layout<*, View> = intrinsicLayout(ProgressBar(context)) { layout ->
        isIndeterminate = true
    }

    override fun progress(progress: ObservableProperty<Float>): Layout<*, View> = intrinsicLayout(ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)) { layout ->
        isIndeterminate = false
        max = 100
        layout.isAttached.bind(progress) {
            this.setProgress((it * 100).toInt(), true)
        }
    }

    override fun scrollBoth(view: Layout<*, View>, amountX: MutableObservableProperty<Float>, amountY: MutableObservableProperty<Float>): Layout<*, View> {
        val adapter = AndroidLayoutAdapter(HorizontalScrollView(context).apply {
            addView(ScrollView(context))
        }, true)
        val intrinsic = IntrinsicDimensionLayouts(adapter.view)
        val layout = Layout(
                viewAdapter = adapter,
                x = intrinsic.x,
                y = intrinsic.y
        )
        adapter.view.addView(LayoutToAndroidView(context).also { it.layout = view })
        return layout
    }

    override fun scrollVertical(view: Layout<*, View>, amount: MutableObservableProperty<Float>): Layout<*, View> {
        val adapter = AndroidLayoutAdapter(ScrollView(context), true)
        val intrinsic = IntrinsicDimensionLayouts(adapter.view)
        val layout = Layout(
                viewAdapter = adapter,
                x = intrinsic.x,
                y = intrinsic.y
        )
        adapter.view.addView(LayoutToAndroidView(context).also { it.layout = view })
        return layout
    }

    override fun scrollHorizontal(view: Layout<*, View>, amount: MutableObservableProperty<Float>): Layout<*, View> {
        val adapter = AndroidLayoutAdapter(HorizontalScrollView(context), true)
        val intrinsic = IntrinsicDimensionLayouts(adapter.view)
        val layout = Layout(
                viewAdapter = adapter,
                x = intrinsic.x,
                y = intrinsic.y
        )
        adapter.view.addView(LayoutToAndroidView(context).also { it.layout = view })
        return layout
    }

    override fun card(view: Layout<*, View>): Layout<*, View> = super.card(view).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewAsBase.elevation = dip * 4
        }
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

    override fun Layout<*, View>.touchable(onNewTouch: (Touch) -> Unit): Layout<*, View> {
        this.viewAsBase.onNewTouch(onNewTouch)
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
    ) = intrinsicLayout(ImageButton(context)) { layout ->
        val padding = (dip * 8).toInt()
        this.setPadding(padding, padding, padding, padding)
        layout.isAttached.bind(label) {
            if (Build.VERSION.SDK_INT > 26) {
                this.tooltipText = it
            }
        }
        layout.isAttached.bind(imageWithSizing) {
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
    ) = intrinsicLayout(ImageButton(context)) { layout ->
        layout.isAttached.bind(label) {
            if (Build.VERSION.SDK_INT > 26) {
                this.tooltipText = it
            }
        }
        layout.isAttached.bind(imageWithSizing) {
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
    ) = intrinsicLayout(FloatingActionButton(context)) { layout ->
        backgroundTintList = theme.importance(importance).androidBackground()
        rippleColor = theme.importance(importance).backgroundHighlighted.toInt()
        layout.isAttached.bind(label) {
            if (Build.VERSION.SDK_INT > 26) {
                this.tooltipText = it
            }
        }
        layout.isAttached.bind(imageWithSizing) {
            setImageDrawable(it.android())
        }
        setOnClickListener { onClick.invoke() }
    }

    inner class StandardListAdapter<T>(
            list: List<T>,
            val parent: TreeObservableProperty,
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
                newLayout.isAttached.parent = this.parent
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
