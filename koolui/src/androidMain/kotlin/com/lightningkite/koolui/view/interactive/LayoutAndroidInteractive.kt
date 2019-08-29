package com.lightningkite.koolui.view.graphics

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.PorterDuff
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lightningkite.koolui.android.*
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextInputType
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.frame
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.view.HasActivityAccess
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.Touch
import com.lightningkite.koolui.views.basic.ViewFactoryBasic
import com.lightningkite.koolui.views.interactive.KeyboardType
import com.lightningkite.koolui.views.interactive.ViewFactoryInteractive
import com.lightningkite.koolui.views.interactive.ViewFactoryInteractiveDefault
import com.lightningkite.lokalize.Locale
import com.lightningkite.lokalize.time.*
import com.lightningkite.lokalize.time.Date
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.lifecycle.listen
import com.lightningkite.reacktive.property.transform
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import kotlin.math.abs

interface LayoutAndroidInteractive
    : ViewFactoryInteractiveDefault<Layout<*, View>>,
        ViewFactoryBasic<Layout<*, View>>,
        LayoutViewWrapper<View>,
        HasActivityAccess,
        Themed {

    override fun button(
            label: ObservableProperty<String>,
            imageWithOptions: ObservableProperty<ImageWithOptions?>,
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
        layout.isAttached.bind(imageWithOptions) {
            setCompoundDrawablesWithIntrinsicBounds(it?.android(), null, null, null)
            layout.requestMeasurement()
        }
        setOnClickListener { onClick.invoke() }
    }

    override fun imageButton(
            imageWithOptions: ObservableProperty<ImageWithOptions>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ) = when (importance) {
        Importance.Low -> imageButtonEmbedded(imageWithOptions, label, importance, onClick)
        Importance.Normal -> imageButtonFAB(imageWithOptions, label, importance, onClick)
        Importance.High -> imageButtonFAB(imageWithOptions, label, importance, onClick)
        Importance.Danger -> imageButtonFAB(imageWithOptions, label, importance, onClick)
    }


    override fun <T> picker(
            options: ObservableList<T>,
            selected: MutableObservableProperty<T>,
            toString: (T) -> String
    ): Layout<Spinner, View> = intrinsicLayout(Spinner(context)) { layout ->
        val newAdapter: StandardListAdapter<T> = StandardListAdapter<T>(options, this@LayoutAndroidInteractive, layout.isAttached, toString)
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
        val layout = Layout.frame(view.adapter(), contains)
        contains.viewAdapter.viewAsBase.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        layout.isAttached.bind(working) {
            view.isRefreshing = it
        }
        view.setOnRefreshListener {
            onRefresh()
        }
        return layout
    }


    fun imageButtonEmbedded(
            imageWithOptions: ObservableProperty<ImageWithOptions>,
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
        layout.isAttached.bind(imageWithOptions) {
            setBackgroundResource(selectableItemBackgroundResource)
            setImageDrawable(it.android())
        }
        setOnClickListener { onClick.invoke() }
    }

    fun imageButtonRect(
            imageWithOptions: ObservableProperty<ImageWithOptions>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ) = intrinsicLayout(ImageButton(context)) { layout ->
        layout.isAttached.bind(label) {
            if (Build.VERSION.SDK_INT > 26) {
                this.tooltipText = it
            }
        }
        layout.isAttached.bind(imageWithOptions) {
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
            imageWithOptions: ObservableProperty<ImageWithOptions>,
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
        layout.isAttached.bind(imageWithOptions) {
            setImageDrawable(it.android())
        }
        setOnClickListener { onClick.invoke() }
    }

    class StandardListAdapter<T>(
            list: List<T>,
            val basic: ViewFactoryBasic<Layout<*, View>>,
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
                val newLayout = basic.text(
                        text = newObs.transform(toString)
                )
                val p = (8 * dip).toInt()
                newLayout.viewAsBase.setPadding(0, p, 0, p)
                newLayout.isAttached.parent = this.parent
                val newView = newLayout.viewAdapter.viewAsBase
                newView.tag = newObs
                newObs.index = position
                newView.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                newView
            } else {
                val obs = convertView.tag as StandardListAdapter<T>.ItemObservable
                obs.index = position
                obs.value = (list[position])
                convertView
            }
        }
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

    override fun Layout<*, View>.acceptCharacterInput(onCharacter: (Char) -> Unit, keyboardType: KeyboardType): Layout<*, View> {
        //TODO: Perhaps we'll do this on Android eventually?
        return this
    }
}