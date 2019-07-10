package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.image.ImageScaleType
import com.lightningkite.koolui.image.ImageWithSizing
import com.lightningkite.koolui.layout.*
import com.lightningkite.koolui.toTimeStamp
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.time.DateTime
import com.lightningkite.lokalize.time.Time
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.lifecycle.listen
import com.lightningkite.reacktive.property.transform
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreGraphics.CGAffineTransformMakeTranslation
import platform.CoreGraphics.CGRect
import platform.Foundation.NSIndexPath
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.*
import platform.darwin.NSObject
import kotlin.math.max
import kotlin.math.round

class UIKitViewFactory(override val theme: Theme, override val colorSet: ColorSet, root: Layout<*, UIView>? = null) : LayoutViewFactory<UIView>(root) {

    override fun withColorSet(colorSet: ColorSet): UIKitViewFactory = UIKitViewFactory(theme, colorSet, root)

    override fun defaultViewContainer(): UIView = UIView(frame = CGRect.zeroVal)

    override fun <SPECIFIC : UIView> SPECIFIC.adapter(): ViewAdapter<SPECIFIC, UIView> = UIViewAdapter(this)

    override fun applyEntranceTransition(view: UIView, animation: Animation) {
        when (animation) {
            Animation.None -> {
            }
            Animation.Push -> {
                val width = view.superview?.frame?.useContents { size.width } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(width, 0.0)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                }
            }
            Animation.Pop -> {
                val width = view.superview?.frame?.useContents { size.width } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(-width, 0.0)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                }
            }
            Animation.MoveUp -> {
                val height = view.superview?.frame?.useContents { size.height } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, -height)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                }
            }
            Animation.MoveDown -> {
                val height = view.superview?.frame?.useContents { size.height } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, height)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                }
            }
            Animation.Fade -> {
                view.alpha = 0.0
                UIView.animateWithDuration(.5) {
                    view.alpha = 1.0
                }
            }
            Animation.Flip -> {
                view.transform = CGAffineTransformMakeScale(1.0, 0.0)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeScale(1.0, 1.0)
                }
            }
        }
    }

    override fun applyExitTransition(view: UIView, animation: Animation, onComplete: () -> Unit) {
        when (animation) {
            Animation.None -> {
            }
            Animation.Push -> {
                val width = view.superview?.frame?.useContents { size.width } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeTranslation(-width, 0.0)
                }
            }
            Animation.Pop -> {
                val width = view.superview?.frame?.useContents { size.width } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeTranslation(width, 0.0)
                }
            }
            Animation.MoveUp -> {
                val height = view.superview?.frame?.useContents { size.height } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeTranslation(0.0, height)
                }
            }
            Animation.MoveDown -> {
                val height = view.superview?.frame?.useContents { size.height } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeTranslation(0.0, -height)
                }
            }
            Animation.Fade -> {
                view.alpha = 1.0
                UIView.animateWithDuration(.5) {
                    view.alpha = 0.0
                }
            }
            Animation.Flip -> {
                view.transform = CGAffineTransformMakeScale(1.0, 1.0)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeScale(1.0, 0.0)
                }
            }
        }
    }

    //Ready

    fun <S : UIView> S.intrinsic(config: S.(Layout<S, UIView>) -> Unit): Layout<S, UIView> {
        val intrinsicLayoutDimensions = IntrinsicLayoutDimensions(this)
        val l = Layout(
                this.adapter,
                intrinsicLayoutDimensions.x,
                intrinsicLayoutDimensions.y
        )
        config(this, l)
        return l
    }

    override fun text(
            text: ObservableProperty<String>,
            importance: Importance,
            size: TextSize,
            align: AlignPair,
            maxLines: Int
    ): Layout<UILabelWithVerticalAlignment, UIView> =
            UILabelWithVerticalAlignment(CGRect.zeroVal).intrinsic { layout ->
                this.numberOfLines = maxLines.toLong()
                this.textAlignment = when (align.horizontal) {
                    Align.Start -> NSTextAlignmentLeft
                    Align.Center -> NSTextAlignmentCenter
                    Align.End -> NSTextAlignmentRight
                    Align.Fill -> NSTextAlignmentJustified
                }
                this.verticalAlignment = align.vertical
                this.font = UIFont.systemFontOfSize(size.ios)
                this.textColor = colorSet.importance(importance).ios

                layout.isAttached.bind(text) {
                    this.text = it
                }
            }

    override fun image(imageWithSizing: ObservableProperty<ImageWithSizing>): Layout<UIImageView, UIView> =
            UIImageView(CGRect.zeroVal).intrinsic { layout ->
                layout.isAttached.bind(imageWithSizing) {
                    this.image = it.image.image
                    when (it.scaleType) {
                        ImageScaleType.Crop -> this.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
                        ImageScaleType.Fill -> this.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                        ImageScaleType.Center -> this.contentMode = UIViewContentMode.UIViewContentModeCenter
                    }
                    //TODO: Default size
                }
            }

    override fun button(
            label: ObservableProperty<String>,
            imageWithSizing: ObservableProperty<ImageWithSizing?>,
            importance: Importance,
            onClick: () -> Unit
    ): Layout<UIButton, UIView> = UIButton(CGRect.zeroVal).intrinsic { layout ->

        this.setTitleColor(colorSet.importance(importance).ios, UIControlStateNormal)
        layout.viewAdapter.addAction(UIControlEventTouchUpInside, onClick)

        layout.isAttached.bind(label) {
            this.setTitle(it, UIControlStateNormal)
        }
        layout.isAttached.bind(imageWithSizing) {
            this.setImage(it?.image?.image, UIControlStateNormal)
            when (it?.scaleType) {
                ImageScaleType.Crop -> this.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
                null, ImageScaleType.Fill -> this.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                ImageScaleType.Center -> this.contentMode = UIViewContentMode.UIViewContentModeCenter
            }
        }
    }

    override fun textField(
            text: MutableObservableProperty<String>,
            placeholder: String,
            type: TextInputType
    ): Layout<UITextField, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = layout.viewAdapter as UIViewAdapter<UITextField>
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder
        this.secureTextEntry = type == TextInputType.Password
        this.keyboardType = when (type) {
            TextInputType.Paragraph,
            TextInputType.Sentence,
            TextInputType.Name,
            TextInputType.CapitalizedIdentifier,
            TextInputType.Address -> UIKeyboardTypeDefault
            TextInputType.Password -> UIKeyboardTypeDefault
            TextInputType.URL -> UIKeyboardTypeURL
            TextInputType.Email -> UIKeyboardTypeEmailAddress
            TextInputType.Phone -> UIKeyboardTypePhonePad
        }
        this.autocapitalizationType = when (type) {
            TextInputType.Paragraph,
            TextInputType.Sentence -> UITextAutocapitalizationType.UITextAutocapitalizationTypeSentences
            TextInputType.Name, TextInputType.Address -> UITextAutocapitalizationType.UITextAutocapitalizationTypeWords
            TextInputType.CapitalizedIdentifier,
            TextInputType.Phone -> UITextAutocapitalizationType.UITextAutocapitalizationTypeAllCharacters
            TextInputType.Password,
            TextInputType.URL,
            TextInputType.Email -> UITextAutocapitalizationType.UITextAutocapitalizationTypeNone
        }
        this.smartQuotesType = UITextSmartQuotesType.UITextSmartQuotesTypeNo
        this.textContentType = when (type) {
            TextInputType.Paragraph,
            TextInputType.Sentence -> null
            TextInputType.Name -> UITextContentTypeName
            TextInputType.Password -> UITextContentTypePassword
            TextInputType.CapitalizedIdentifier -> null
            TextInputType.URL -> UITextContentTypeURL
            TextInputType.Email -> UITextContentTypeEmailAddress
            TextInputType.Phone -> UITextContentTypeTelephoneNumber
            TextInputType.Address -> UITextContentTypeFullStreetAddress
        }
        this.spellCheckingType = when (type) {
            TextInputType.Paragraph,
            TextInputType.Sentence -> UITextSpellCheckingType.UITextSpellCheckingTypeYes
            TextInputType.Address,
            TextInputType.Name -> UITextSpellCheckingType.UITextSpellCheckingTypeDefault
            TextInputType.Password,
            TextInputType.CapitalizedIdentifier,
            TextInputType.URL,
            TextInputType.Email,
            TextInputType.Phone -> UITextSpellCheckingType.UITextSpellCheckingTypeNo
        }

        layout.isAttached.bind(text) {
            if (it != this.text) {
                this.text = it
            }
        }
        adapter.addAction(UIControlEventValueChanged) {
            val newValue = this.text ?: ""
            if (newValue != text.value) {
                text.value = newValue
            }
        }
    }

    override fun numberField(
            value: MutableObservableProperty<Double?>,
            placeholder: String,
            allowNegatives: Boolean,
            decimalPlaces: Int
    ): Layout<UITextField, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = layout.viewAdapter as UIViewAdapter<UITextField>
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder
        this.keyboardType = UIKeyboardTypeNumbersAndPunctuation

        layout.isAttached.bind(value) {
            if (it != this.text?.toDoubleOrNull() && it != null) {
                this.text = it.toString()
            }
        }
        adapter.addAction(UIControlEventValueChanged) {
            val newValue = this.text?.toDoubleOrNull()
            if (newValue != value.value) {
                value.value = newValue
            }
        }
    }

    override fun integerField(value: MutableObservableProperty<Long?>, placeholder: String, allowNegatives: Boolean): Layout<*, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = layout.viewAdapter as UIViewAdapter<UITextField>
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder
        this.keyboardType = if (allowNegatives) UIKeyboardTypeNumbersAndPunctuation else UIKeyboardTypeNumberPad

        layout.isAttached.bind(value) {
            if (it != this.text?.toLongOrNull() && it != null) {
                this.text = it.toString()
            }
        }
        adapter.addAction(UIControlEventValueChanged) {
            val newValue = this.text?.toLongOrNull()
            if (newValue != value.value) {
                value.value = newValue
            }
        }
    }

    override fun toggle(observable: MutableObservableProperty<Boolean>): Layout<UISwitch, UIView> = UISwitch(CGRect.zeroVal).intrinsic { layout ->
        this.onTintColor = colorSet.backgroundHighlighted.ios
        this.tintColor = colorSet.backgroundDisabled.ios
        this.thumbTintColor = colorSet.foreground.ios

        layout.isAttached.bind(observable) {
            if (it != this.on) {
                this.on = it
            }
        }
        layout.viewAdapter.addAction(UIControlEventValueChanged) {
            if (this.on != observable.value) {
                observable.value = this.on
            }
        }
    }

    override fun imageButton(
            imageWithSizing: ObservableProperty<ImageWithSizing>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ): Layout<*, UIView> = button(
            label = ConstantObservableProperty(""),
            imageWithSizing = imageWithSizing.transform { it },
            importance = importance,
            onClick = onClick
    )

    override fun Layout<*, UIView>.alpha(alpha: ObservableProperty<Float>): Layout<*, UIView> {
        this.isAttached.bind(alpha) {
            viewAdapter.viewAsBase.alpha = it.toDouble()
        }
        return this
    }

    override fun Layout<*, UIView>.altClickable(onAltClick: () -> Unit): Layout<*, UIView> {
        viewAdapter.addGestureRecognizer(UILongPressGestureRecognizer(), onAltClick)
        return this
    }

    override fun Layout<*, UIView>.clickable(onClick: () -> Unit): Layout<*, UIView> {
        viewAdapter.addGestureRecognizer(UITapGestureRecognizer(), onClick)
        return this
    }

    override fun Layout<*, UIView>.background(color: ObservableProperty<Color>): Layout<*, UIView> {
        this.isAttached.bind(color) {
            viewAdapter.viewAsBase.backgroundColor = it.ios
        }
        return this
    }


    override fun <T> picker(
            options: ObservableList<T>,
            selected: MutableObservableProperty<T>,
            toString: (T) -> String
    ): Layout<*, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = (layout.viewAdapter as UIViewAdapter<UITextField>)
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder

        adapter.addAction(UIControlEventTouchDown) { becomeFirstResponder() }

        inputView = makeUIPickerView(toString, options, selected, adapter)
        inputAccessoryView = adapter.toolbarWithDoneButton()

    }

    override fun timePicker(observable: MutableObservableProperty<Time>): Layout<*, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = (layout.viewAdapter as UIViewAdapter<UITextField>)
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder

        val picker = UIDatePicker(CGRect.zeroVal)
        picker.datePickerMode = UIDatePickerMode.UIDatePickerModeTime
        adapter.addAction(picker, UIControlEventValueChanged) {
            observable.value = picker.date.toTimeStamp().time()
        }
        adapter.addAction(UIControlEventTouchDown) { becomeFirstResponder() }

        inputView = picker
        inputAccessoryView = adapter.toolbarWithDoneButton()
    }

    override fun datePicker(observable: MutableObservableProperty<Date>): Layout<*, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = (layout.viewAdapter as UIViewAdapter<UITextField>)
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder

        val picker = UIDatePicker(CGRect.zeroVal)
        picker.datePickerMode = UIDatePickerMode.UIDatePickerModeDate
        adapter.addAction(picker, UIControlEventValueChanged) {
            observable.value = picker.date.toTimeStamp().date()
        }

        adapter.addAction(UIControlEventTouchDown) { becomeFirstResponder() }

        inputView = picker
        inputAccessoryView = adapter.toolbarWithDoneButton()
    }

    override fun dateTimePicker(observable: MutableObservableProperty<DateTime>): Layout<*, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = (layout.viewAdapter as UIViewAdapter<UITextField>)
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder

        val picker = UIDatePicker(CGRect.zeroVal)
        picker.datePickerMode = UIDatePickerMode.UIDatePickerModeDateAndTime
        adapter.addAction(picker, UIControlEventValueChanged) {
            observable.value = picker.date.toTimeStamp().dateTime()
        }

        adapter.addAction(UIControlEventTouchDown) { becomeFirstResponder() }

        inputView = picker
        inputAccessoryView = adapter.toolbarWithDoneButton()
    }

    override fun card(view: Layout<*, UIView>): Layout<*, UIView> {
        return super.card(view).apply {
            view.viewAdapter.viewAsBase.layer.apply {
                shadowOpacity = .5f
                shadowRadius = 4.0
                shadowColor = UIColor.blackColor.CGColor
            }
        }
    }

    override fun web(content: ObservableProperty<String>): Layout<*, UIView> = UIWebView(CGRect.zeroVal).intrinsic { layout ->
        layout.isAttached.bind(content) {
            if (it.startsWith("http")) {
                val url = NSURL.URLWithString(it) ?: return@bind
                loadRequest(NSURLRequest.requestWithURL(url))
            } else {
                loadHTMLString(it, null)
            }
        }
    }

    override fun textArea(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): Layout<*, UIView> = UITextView(CGRect.zeroVal).intrinsic { layout ->
        this.textColor = colorSet.foreground.ios
        this.secureTextEntry = type == TextInputType.Password
        this.keyboardType = when (type) {
            TextInputType.Paragraph,
            TextInputType.Sentence,
            TextInputType.Name,
            TextInputType.CapitalizedIdentifier,
            TextInputType.Address -> UIKeyboardTypeDefault
            TextInputType.Password -> UIKeyboardTypeDefault
            TextInputType.URL -> UIKeyboardTypeURL
            TextInputType.Email -> UIKeyboardTypeEmailAddress
            TextInputType.Phone -> UIKeyboardTypePhonePad
        }
        this.autocapitalizationType = when (type) {
            TextInputType.Paragraph,
            TextInputType.Sentence -> UITextAutocapitalizationType.UITextAutocapitalizationTypeSentences
            TextInputType.Name, TextInputType.Address -> UITextAutocapitalizationType.UITextAutocapitalizationTypeWords
            TextInputType.CapitalizedIdentifier,
            TextInputType.Phone -> UITextAutocapitalizationType.UITextAutocapitalizationTypeAllCharacters
            TextInputType.Password,
            TextInputType.URL,
            TextInputType.Email -> UITextAutocapitalizationType.UITextAutocapitalizationTypeNone
        }
        this.smartQuotesType = UITextSmartQuotesType.UITextSmartQuotesTypeNo
        this.textContentType = when (type) {
            TextInputType.Paragraph,
            TextInputType.Sentence -> null
            TextInputType.Name -> UITextContentTypeName
            TextInputType.Password -> UITextContentTypePassword
            TextInputType.CapitalizedIdentifier -> null
            TextInputType.URL -> UITextContentTypeURL
            TextInputType.Email -> UITextContentTypeEmailAddress
            TextInputType.Phone -> UITextContentTypeTelephoneNumber
            TextInputType.Address -> UITextContentTypeFullStreetAddress
        }
        this.spellCheckingType = when (type) {
            TextInputType.Paragraph,
            TextInputType.Sentence -> UITextSpellCheckingType.UITextSpellCheckingTypeYes
            TextInputType.Address,
            TextInputType.Name -> UITextSpellCheckingType.UITextSpellCheckingTypeDefault
            TextInputType.Password,
            TextInputType.CapitalizedIdentifier,
            TextInputType.URL,
            TextInputType.Email,
            TextInputType.Phone -> UITextSpellCheckingType.UITextSpellCheckingTypeNo
        }

        layout.isAttached.bind(text) {
            if (it != this.text) {
                this.text = it
            }
        }
        val adapter = layout.viewAdapter as UIViewAdapter<UITextView>
        val dg: UITextViewDelegateProtocol = object : NSObject(), UITextViewDelegateProtocol {
            override fun textViewDidChange(textView: UITextView) {
                val newValue = this@intrinsic.text
                if (newValue != text.value) {
                    text.value = newValue
                }
            }
        }
        adapter.holding["delegate"] = dg
        this.delegate = dg
    }

    override fun slider(range: IntRange, observable: MutableObservableProperty<Int>): Layout<*, UIView> = UISlider(CGRect.zeroVal).intrinsic { layout ->
        this.minimumValue = range.start.toFloat()
        this.maximumValue = range.endInclusive.toFloat()
        layout.isAttached.bind(observable) {
            val intValue = round(this.value).toInt()
            if (it != intValue) {
                this.value = it.toFloat()
            }
        }
        layout.viewAdapter.addAction(UIControlEventValueChanged) {
            val intValue = round(this.value).toInt()
            if (intValue != observable.value) {
                observable.value = intValue
            }
        }
    }

    class ScrollDimensionLayout(val subview: DimensionLayout) : BaseDimensionLayout() {
        override fun measure(output: Measurement) {
            output.size = 100f
            output.startMargin = subview.measurement.startMargin
            output.endMargin = subview.measurement.endMargin
        }

        override fun layoutChildren(size: Float) {
            subview.layout(0f, max(size, subview.measurement.size))
        }

        override val childSequence: Sequence<DimensionLayout> get() = sequenceOf(subview)
    }

    override fun scrollBoth(view: Layout<*, UIView>, amountX: MutableObservableProperty<Float>, amountY: MutableObservableProperty<Float>): Layout<*, UIView> {
        val adapter = UIScrollView().adapter()
        return Layout(
                viewAdapter = adapter,
                x = ScrollDimensionLayout(view.x),
                y = ScrollDimensionLayout(view.y)
        )
    }

    override fun scrollHorizontal(view: Layout<*, UIView>, amount: MutableObservableProperty<Float>): Layout<*, UIView> {
        val adapter = UIScrollView().adapter()
        return Layout(
                viewAdapter = adapter,
                x = ScrollDimensionLayout(view.x),
                y = AlignDimensionLayout(listOf(Align.Fill to view.y))
        )
    }

    override fun scrollVertical(view: Layout<*, UIView>, amount: MutableObservableProperty<Float>): Layout<*, UIView> {
        val adapter = UIScrollView().adapter()
        return Layout(
                viewAdapter = adapter,
                x = AlignDimensionLayout(listOf(Align.Fill to view.x)),
                y = ScrollDimensionLayout(view.y)
        )
    }

    override fun <T> list(
            data: ObservableList<T>,
            firstIndex: MutableObservableProperty<Int>,
            lastIndex: MutableObservableProperty<Int>,
            direction: Direction,
            makeView: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> Layout<*, UIView>
    ): Layout<*, UIView> = UITableView(CGRect.zeroVal).intrinsic { layout ->
        this.allowsSelection = false
        this.allowsMultipleSelection = false

        val source = ListDataSource(data = data, makeLayout = makeView)
        this.dataSource = source
        (adapter as UIViewAdapter<*>).holding["dataSource"] = source

        layout.isAttached.listen(data.onListAdd) { _, index ->
            this.insertRowsAtIndexPaths(
                    indexPaths = listOf(NSIndexPath.indexPathForRow(row = index.toLong(), inSection = 0)),
                    withRowAnimation = UITableViewRowAnimationAutomatic
            )
        }
        layout.isAttached.listen(data.onListChange) { _, _, index ->
            this.reloadRowsAtIndexPaths(
                    indexPaths = listOf(NSIndexPath.indexPathForRow(row = index.toLong(), inSection = 0)),
                    withRowAnimation = UITableViewRowAnimationAutomatic
            )
        }
        layout.isAttached.listen(data.onListMove) { _, oldIndex, index ->
            this.reloadRowsAtIndexPaths(
                    indexPaths = listOf(
                            NSIndexPath.indexPathForRow(row = oldIndex.toLong(), inSection = 0),
                            NSIndexPath.indexPathForRow(row = index.toLong(), inSection = 0)
                    ),
                    withRowAnimation = UITableViewRowAnimationAutomatic
            )
        }
        layout.isAttached.listen(data.onListRemove) { _, index ->
            this.deleteRowsAtIndexPaths(
                    indexPaths = listOf(NSIndexPath.indexPathForRow(row = index.toLong(), inSection = 0)),
                    withRowAnimation = UITableViewRowAnimationAutomatic
            )
        }
        layout.isAttached.listen(data.onListReplace) {
            this.reloadData()
        }
    }

    override fun refresh(contains: Layout<*, UIView>, working: ObservableProperty<Boolean>, onRefresh: () -> Unit): Layout<*, UIView> {
        (contains.view as? UIScrollView)?.let { scroll ->
            scroll.refreshControl = UIRefreshControl().apply {
                contains.isAttached.bind(working) {
                    if (it) {
                        beginRefreshing()
                    } else {
                        endRefreshing()
                    }
                }
                (contains.viewAdapter as UIViewAdapter<*>).addAction(this, UIControlEventValueChanged, onRefresh)
            }
            return contains
        }
        return super.refresh(contains, working, onRefresh)
    }

//    override fun <DEPENDENCY> pages(dependency: DEPENDENCY, page: MutableObservableProperty<Int>, vararg pageGenerator: ViewGenerator<DEPENDENCY, Layout<*, UIView>>): Layout<*, UIView> {
//        return super.pages(dependency, page, *pageGenerator)
//    }

    override fun tabs(options: ObservableList<TabItem>, selected: MutableObservableProperty<TabItem>): Layout<*, UIView> = UITabBar(CGRect.zeroVal).intrinsic { layout ->
        layout.isAttached.bind(options.onListUpdate) {
            setItems(items = it.mapIndexed { index, it ->
                UITabBarItem(
                        title = it.text,
                        image = it.imageWithSizing.image.image,
                        selectedImage = it.imageWithSizing.image.image
                ).apply {
                    this.tag = index.toLong()
                }
            }, animated = true)
        }
        val delegate = object : NSObject(), UITabBarDelegateProtocol {
            override fun tabBar(tabBar: UITabBar, didSelectItem: UITabBarItem) {
                selected.value = options[didSelectItem.tag.toInt()]
            }
        }
        (layout.viewAdapter as UIViewAdapter<*>).holding["delegate"] = delegate
        this.delegate = delegate

        this.barTintColor = theme.bar.background.ios
        this.selectedImageTintColor = theme.bar.foregroundHighlighted.ios
        this.unselectedItemTintColor = theme.bar.foregroundDisabled.ios
        this.itemPositioning = UITabBarItemPositioning.UITabBarItemPositioningAutomatic
    }

    override fun progress(progress: ObservableProperty<Float>): Layout<*, UIView> = UIProgressView(CGRect.zeroVal).intrinsic { layout ->
        layout.isAttached.bind(progress) {
            this.progress = it
        }
    }

    override fun work(): Layout<*, UIView> = UIActivityIndicatorView(CGRect.zeroVal).intrinsic { layout -> this.startAnimating() }
}


