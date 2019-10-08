package com.lightningkite.koolui.views.ios

import com.lightningkite.kommon.collection.pop
import com.lightningkite.kommon.collection.reset
import com.lightningkite.koolui.builders.button
import com.lightningkite.koolui.builders.horizontal
import com.lightningkite.koolui.builders.space
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.image.*
import com.lightningkite.koolui.concepts.lastOrNullObservableWithAnimations
import com.lightningkite.koolui.layout.*
import com.lightningkite.koolui.toNSDate
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.lokalize.time.*
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.lastOrNullObservable
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.transform
import com.lightningkite.recktangle.Point
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import platform.CoreFoundation.CFNotificationCenterAddObserver
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreGraphics.CGAffineTransformMakeTranslation
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject
import platform.darwin.object_getClassName
import kotlin.math.max
import kotlin.math.round

class UIKitViewFactory(
        val closureSleeveProvider: (() -> Unit) -> NSObject,
        override val theme: Theme,
        override val colorSet: ColorSet,
        root: Layout<*, UIView>? = null
) : LayoutViewFactory<UIView>(root) {

    override fun canvas(
            draw: ObservableProperty<Canvas.()->Unit>
    ): Layout<*, UIView>

    override fun withColorSet(colorSet: ColorSet): UIKitViewFactory = UIKitViewFactory(closureSleeveProvider, theme, colorSet, root)

    override fun defaultViewContainer(): UIView = UIView(frame = CGRect.zeroVal).apply {
        clipsToBounds = true
    }

    override fun <SPECIFIC : UIView> SPECIFIC.adapter(): UIViewAdapter<SPECIFIC> = UIViewAdapter(this)

    val duration = .25

    override fun <DEPENDENCY> window(
            dependency: DEPENDENCY,
            stack: MutableObservableList<ViewGenerator<DEPENDENCY, Layout<*, UIView>>>,
            tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, Layout<*, UIView>>>>
    ): Layout<*, UIView> = vertical {

        -space(
                width = 4f,
                height = UIApplication.sharedApplication.statusBarFrame.useContents { size.height }.toFloat()
        ).background(theme.bar.background)

        -with(withColorSet(theme.bar)) {
            frame(horizontal {
                defaultAlign = Align.Center
                -button(
                        label = "< Back",
                        importance = Importance.Low,
                        onClick = { if (stack.size > 1) stack.pop() }
                ).alpha(stack.onListUpdate.transform { if (it.size > 1) 1f else 0f })
//                -imageButton(
//                        imageWithOptions = ConstantObservableProperty(
//                                MaterialIcon.arrowBack.color(theme.bar.foreground).withSizing(
//                                        Point(
//                                                24f,
//                                                24f
//                                        )
//                                )
//                        ),
//                        importance = Importance.Low,
//                        onClick = { if (stack.size > 1) stack.pop() }
//                ).alpha(stack.onListUpdate.transform { if (it.size > 1) 1f else 0f })

                -text(text = stack.onListUpdate.transform { it.lastOrNull()?.title ?: "" }, size = TextSize.Header)

                +space(Point(5f, 5f))

                -swap(stack.lastOrNullObservable().transform {
                    (it?.generateActions(dependency) ?: space(1f)) to Animation.Fade
                })
            }).background(theme.bar.background)
        }

        +frame(swap(stack.lastOrNullObservableWithAnimations().transform {
            (it.first?.generate(dependency) ?: space(Point.zero)) to it.second
        })
        ).background(theme.main.background)

        if (!tabs.isEmpty()) {
            -frame(horizontal {
                for (tab in tabs) {
                    +button(tab.first.text, tab.first.imageWithOptions) {
                        stack.reset(tab.second)
                    }
                }
            }).background(theme.bar.background)
        }
    }

    override fun applyEntranceTransition(view: UIView, animation: Animation) {
        when (animation) {
            Animation.None -> {
            }
            Animation.Push -> {
                val width = view.superview?.frame?.useContents { size.width } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(width, 0.0)
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                        },
                        completion = {
                        }
                )
            }
            Animation.Pop -> {
                val width = view.superview?.frame?.useContents { size.width } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(-width, 0.0)
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                        },
                        completion = {
                        }
                )
            }
            Animation.MoveUp -> {
                val height = view.superview?.frame?.useContents { size.height } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, -height)
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                        },
                        completion = {
                        }
                )
            }
            Animation.MoveDown -> {
                val height = view.superview?.frame?.useContents { size.height } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, height)
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                        },
                        completion = {
                        }
                )
            }
            Animation.Fade -> {
                view.alpha = 0.0
                UIView.animateWithDuration(.5) {
                    view.alpha = 1.0
                }
            }
            Animation.Flip -> {
                view.transform = CGAffineTransformMakeScale(1.0, 0.001)
                UIView.animateWithDuration(.5) {
                    view.transform = CGAffineTransformMakeScale(1.0, 1.0)
                }
            }
        }
    }

    override fun applyExitTransition(view: UIView, animation: Animation, onComplete: () -> Unit) {
        when (animation) {
            Animation.None -> {
                onComplete()
            }
            Animation.Push -> {
                val width = view.superview?.frame?.useContents { size.width } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.transform = CGAffineTransformMakeTranslation(-width, 0.0)
                        },
                        completion = {
                            onComplete()
                        }
                )
            }
            Animation.Pop -> {
                val width = view.superview?.frame?.useContents { size.width } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.transform = CGAffineTransformMakeTranslation(width, 0.0)
                        },
                        completion = {
                            onComplete()
                        }
                )
            }
            Animation.MoveUp -> {
                val height = view.superview?.frame?.useContents { size.height } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.transform = CGAffineTransformMakeTranslation(0.0, height)
                        },
                        completion = {
                            onComplete()
                        }
                )
            }
            Animation.MoveDown -> {
                val height = view.superview?.frame?.useContents { size.height } ?: 0.0
                view.transform = CGAffineTransformMakeTranslation(0.0, 0.0)
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.transform = CGAffineTransformMakeTranslation(0.0, -height)
                        },
                        completion = {
                            onComplete()
                        }
                )
            }
            Animation.Fade -> {
                view.alpha = 1.0
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.alpha = 0.0
                        },
                        completion = {
                            onComplete()
                        }
                )
            }
            Animation.Flip -> {
                view.transform = CGAffineTransformMakeScale(1.0, 1.0)
                UIView.animateWithDuration(
                        duration = duration,
                        animations = {
                            view.transform = CGAffineTransformMakeScale(1.0, 0.001)
                        },
                        completion = {
                            onComplete()
                        }
                )
            }
        }
    }

    //Ready

    fun <S : UIView> S.intrinsic(config: S.(Layout<S, UIView>) -> Unit): Layout<S, UIView> {
        val intrinsicLayoutDimensions = IntrinsicLayoutDimensions(this)
        val l = Layout(
                this.adapter(),
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

    override fun image(imageWithOptions: ObservableProperty<ImageWithOptions>): Layout<UIImageView, UIView> =
            UIImageView(CGRect.zeroVal).intrinsic { layout ->
                layout.isAttached.bind(imageWithOptions) {
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
            imageWithOptions: ObservableProperty<ImageWithOptions?>,
            importance: Importance,
            onClick: () -> Unit
    ): Layout<UIButton, UIView> = UIButton(CGRect.zeroVal).intrinsic { layout ->
        this.setTitleColor(colorSet.importance(importance).ios, UIControlStateNormal)
        layout.viewAdapter.addAction(UIControlEventTouchUpInside, closureSleeveProvider(onClick))

        layout.isAttached.bind(label) {
            this.setTitle(it, UIControlStateNormal)
        }
        layout.isAttached.bind(imageWithOptions) {
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
        adapter.addAction(UIControlEventValueChanged, closureSleeveProvider {
            val newValue = this.text ?: ""
            if (newValue != text.value) {
                text.value = newValue
            }
        })
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
        adapter.addAction(UIControlEventValueChanged, closureSleeveProvider {
            val newValue = this.text?.toDoubleOrNull()
            if (newValue != value.value) {
                value.value = newValue
            }
        })
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
        adapter.addAction(UIControlEventValueChanged, closureSleeveProvider {
            val newValue = this.text?.toLongOrNull()
            if (newValue != value.value) {
                value.value = newValue
            }
        })
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
        layout.viewAdapter.addAction(UIControlEventValueChanged, closureSleeveProvider {
            if (this.on != observable.value) {
                observable.value = this.on
            }
        })
    }

    override fun imageButton(
            imageWithOptions: ObservableProperty<ImageWithOptions>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ): Layout<*, UIView> = button(
            label = ConstantObservableProperty(""),
            imageWithOptions = imageWithOptions.transform { it },
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
        viewAdapter.addGestureRecognizer(UILongPressGestureRecognizer(), closureSleeveProvider(onAltClick))
        return this
    }

    override fun Layout<*, UIView>.clickable(onClick: () -> Unit): Layout<*, UIView> {
        viewAdapter.addGestureRecognizer(UITapGestureRecognizer(), closureSleeveProvider(onClick))
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

        adapter.addAction(UIControlEventTouchDown, closureSleeveProvider { becomeFirstResponder() })

        inputView = makeUIPickerView({
            val base = toString(it)
            NSAttributedString.create(string = base, attributes = mapOf<Any?, Any?>(NSForegroundColorAttributeName to colorSet.foreground.ios))
        }, options, selected, adapter).apply {
            backgroundColor = colorSet.background.ios
        }
        inputAccessoryView = adapter.toolbarWithDoneButton(closureSleeveProvider)

        layout.isAttached.bind(selected) {
            this.text = toString(it)
        }
    }

    override fun timePicker(observable: MutableObservableProperty<Time>): Layout<*, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = (layout.viewAdapter as UIViewAdapter<UITextField>)
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder

        val picker = UIDatePicker(CGRect.zeroVal)
        picker.backgroundColor = colorSet.background.ios
        picker.setValue(value = colorSet.foreground.ios, forKeyPath = "textColor")
        picker.datePickerMode = UIDatePickerMode.UIDatePickerModeTime
        adapter.addAction(picker, UIControlEventValueChanged, closureSleeveProvider {
            observable.value = picker.date.toTimeStamp().time()
        })
        layout.isAttached.bind(observable) {
            picker.date = DateTime(TimeStamp.now().date(), it).toTimeStamp().toNSDate()
        }
        adapter.addAction(UIControlEventTouchDown, closureSleeveProvider { becomeFirstResponder() })

        layout.isAttached.bind(observable) {
            this.text = it.toString()
        }

        inputView = picker
        inputAccessoryView = adapter.toolbarWithDoneButton(closureSleeveProvider)
    }

    override fun datePicker(observable: MutableObservableProperty<Date>): Layout<*, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = (layout.viewAdapter as UIViewAdapter<UITextField>)
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder

        val picker = UIDatePicker(CGRect.zeroVal)
        picker.backgroundColor = colorSet.background.ios
        picker.setValue(value = colorSet.foreground.ios, forKeyPath = "textColor")
        picker.datePickerMode = UIDatePickerMode.UIDatePickerModeDate
        adapter.addAction(picker, UIControlEventValueChanged, closureSleeveProvider {
            observable.value = picker.date.toTimeStamp().date()
        })
        layout.isAttached.bind(observable) {
            picker.date = DateTime(it, Time(0)).toTimeStamp().toNSDate()
        }
        adapter.addAction(UIControlEventTouchDown, closureSleeveProvider { becomeFirstResponder() })

        layout.isAttached.bind(observable) {
            this.text = it.toString()
        }

        inputView = picker
        inputAccessoryView = adapter.toolbarWithDoneButton(closureSleeveProvider)
    }

    override fun dateTimePicker(observable: MutableObservableProperty<DateTime>): Layout<*, UIView> = UITextField(CGRect.zeroVal).intrinsic { layout ->
        val adapter = (layout.viewAdapter as UIViewAdapter<UITextField>)
        val dg = UITextFieldDoneDelegate()
        adapter.holding["delegate"] = dg
        delegate = dg

        this.textColor = colorSet.foreground.ios
        this.placeholder = placeholder

        val picker = UIDatePicker(CGRect.zeroVal)
        picker.backgroundColor = colorSet.background.ios
        picker.setValue(value = colorSet.foreground.ios, forKeyPath = "textColor")
        picker.datePickerMode = UIDatePickerMode.UIDatePickerModeDateAndTime
        adapter.addAction(picker, UIControlEventValueChanged, closureSleeveProvider {
            observable.value = picker.date.toTimeStamp().dateTime()
        })
        layout.isAttached.bind(observable) {
            picker.date = it.toTimeStamp().toNSDate()
        }
        adapter.addAction(UIControlEventTouchDown, closureSleeveProvider { becomeFirstResponder() })

        layout.isAttached.bind(observable) {
            this.text = it.toString()
        }

        inputView = picker
        inputAccessoryView = adapter.toolbarWithDoneButton(closureSleeveProvider)
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

    override fun textArea(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): Layout<*, UIView> {
        val view = UITextView(CGRect.zeroVal)
        val layout = Layout(
                viewAdapter = view.adapter(),
                x = LeafDimensionLayout(8f, 80f, 8f),
                y = LeafDimensionLayout(8f, 80f, 8f)
        )
        with(view) {
            this.inputAccessoryView = (layout.viewAdapter as UIViewAdapter<*>).toolbarWithDoneButton(closureSleeveProvider)
            this.backgroundColor = colorSet.background.ios
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
                    val newValue = this@with.text
                    if (newValue != text.value) {
                        text.value = newValue
                    }
                }
            }
            adapter.holding["delegate"] = dg
            this.delegate = dg
        }
        return layout
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
        layout.viewAdapter.addAction(UIControlEventValueChanged, closureSleeveProvider {
            val intValue = round(this.value).toInt()
            if (intValue != observable.value) {
                observable.value = intValue
            }
        })
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
        val scroll = UIScrollView()
        val adapter = scroll.adapter()
        val layout = Layout(
                viewAdapter = adapter,
                x = ScrollDimensionLayout(view.x),
                y = ScrollDimensionLayout(view.y)
        )
        (view.viewAdapter as UIViewAdapter<*>).onResize.add {
            scroll.setContentSize(CGSizeMake(
                    width = it.width.toDouble(),
                    height = it.height.toDouble()
            ))
        }
        layout.addChild(view)
        return layout
    }

    override fun scrollHorizontal(view: Layout<*, UIView>, amount: MutableObservableProperty<Float>): Layout<*, UIView> {
        val scroll = UIScrollView()
        val adapter = scroll.adapter()
        val layout = Layout(
                viewAdapter = adapter,
                x = ScrollDimensionLayout(view.x),
                y = AlignDimensionLayout(listOf(Align.Fill to view.y))
        )
        (view.viewAdapter as UIViewAdapter<*>).onResize.add {
            scroll.setContentSize(CGSizeMake(
                    width = it.width.toDouble(),
                    height = it.height.toDouble()
            ))
        }
        layout.addChild(view)
        return layout
    }

    override fun scrollVertical(view: Layout<*, UIView>, amount: MutableObservableProperty<Float>): Layout<*, UIView> {
        val scroll = UIScrollView()
        val adapter = scroll.adapter()
        val layout = Layout(
                viewAdapter = adapter,
                x = AlignDimensionLayout(listOf(Align.Fill to view.x)),
                y = ScrollDimensionLayout(view.y)
        )
        (view.viewAdapter as UIViewAdapter<*>).onResize.add {
            scroll.setContentSize(CGSizeMake(
                    width = it.width.toDouble(),
                    height = it.height.toDouble()
            ))
        }
        layout.addChild(view)
        return layout
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
        this.rowHeight = UITableViewAutomaticDimension
        this.separatorStyle = UITableViewCellSeparatorStyleNone

        backgroundColor = UIColor.clearColor

        val source = ListDataSource(parentLayout = layout, data = data, makeLayout = makeView)
        this.dataSource = source
        (layout.viewAdapter as UIViewAdapter<*>).holding["dataSource"] = source

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
                (contains.viewAdapter as UIViewAdapter<*>).addAction(this, UIControlEventValueChanged, closureSleeveProvider(onRefresh))
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
                        image = it.imageWithOptions.image.image,
                        selectedImage = it.imageWithOptions.image.image
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

    override fun space(size: Point): Layout<*, UIView> {
        if (size.x == 2f && size.y == 2f) {
            debugHierarchy()
        }
        return super.space(size)
    }

    private fun debugHierarchy(from: UIView? = this.root?.viewAsBase) {
        fun UIView.print(prefix: String = "") {
            println(prefix + object_getClassName(this)?.toKString() + this.frame.useContents { " (${this.origin.x} ${this.origin.y} ${this.size.width} ${this.size.height})" })
            for (child in subviews) {
                if (child is UIView) {
                    child.print(prefix + "  ")
                }
            }
        }
        println("View Hierarchy:")
        from?.print()
    }
}


