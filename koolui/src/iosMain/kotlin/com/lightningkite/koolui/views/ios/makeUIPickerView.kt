package com.lightningkite.koolui.views.ios

import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.MutableObservableProperty
import platform.CoreGraphics.CGRect
import platform.UIKit.UIPickerView
import platform.UIKit.UIPickerViewDataSourceProtocol
import platform.UIKit.UIPickerViewDelegateProtocol
import platform.UIKit.UITextField
import platform.darwin.NSInteger
import platform.darwin.NSObject


fun <T> makeUIPickerView(toString: (T) -> String, options: ObservableList<T>, selected: MutableObservableProperty<T>, adapter: UIViewAdapter<UITextField>): UIPickerView =
        UIPickerView(CGRect.zeroVal).apply {
            @Suppress("CONFLICTING_OVERLOADS")
            val dg = object : NSObject(), UIPickerViewDelegateProtocol {

                override fun pickerView(pickerView: UIPickerView, titleForRow: NSInteger, forComponent: NSInteger): String? {
                    return toString(options[titleForRow.toInt()])
                }

                override fun pickerView(pickerView: UIPickerView, didSelectRow: NSInteger, inComponent: NSInteger) {
                    selected.value = options[didSelectRow.toInt()]
                }
            }
            @Suppress("CONFLICTING_OVERLOADS")
            val ds = object : NSObject(), UIPickerViewDataSourceProtocol {
                override fun pickerView(pickerView: UIPickerView, numberOfRowsInComponent: NSInteger): NSInteger {
                    return options.size.toLong()
                }

                override fun numberOfComponentsInPickerView(pickerView: UIPickerView): NSInteger {
                    return 1
                }
            }
            delegate = dg
            dataSource = ds
            adapter.holding["pickerDelegate"] = dg
            adapter.holding["pickerDataSource"] = ds
        }
