package com.lightningkite.koolui.test.models

data class Post(
        var id: Long? = null,
        var userId: Long = 0,
        var title: String = "",
        var body: String = ""
)