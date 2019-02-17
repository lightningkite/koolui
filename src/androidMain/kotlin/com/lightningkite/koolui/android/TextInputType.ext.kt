package com.lightningkite.koolui.android

import android.text.InputType
import com.lightningkite.koolui.concepts.TextInputType

fun TextInputType.android(): Int = when (this) {
    TextInputType.Paragraph -> InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
            InputType.TYPE_TEXT_FLAG_AUTO_CORRECT or
            InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE or
            InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE or
            InputType.TYPE_TEXT_FLAG_MULTI_LINE
    TextInputType.Name -> InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_FLAG_CAP_WORDS or
            InputType.TYPE_TEXT_VARIATION_PERSON_NAME
    TextInputType.Password -> InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PASSWORD
    TextInputType.Sentence -> InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
            InputType.TYPE_TEXT_FLAG_AUTO_CORRECT or
            InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE or
            InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
    TextInputType.CapitalizedIdentifier -> InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
    TextInputType.URL -> InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_URI
    TextInputType.Email -> InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    TextInputType.Phone -> InputType.TYPE_CLASS_PHONE
    TextInputType.Address -> InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
}