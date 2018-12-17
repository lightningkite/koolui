package com.lightningkite.koolui

data class Notification(
    val id: Int = 0,
    val priority: Float = .5f,
    val title: String = "",
    val content: String = "",
    val image: String? = null,
    val action: String = "",
    val actions: Map<String, String> = mapOf()
) {
    //Actions:
    // Text input
    // Bring to foreground
    // Don't bring to foreground
}
