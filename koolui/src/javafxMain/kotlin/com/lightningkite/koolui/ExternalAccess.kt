package com.lightningkite.koolui

import com.lightningkite.kommon.string.MediaTypeAcceptWithDescription
import com.lightningkite.kommon.string.MediaTypeWithDescription
import com.lightningkite.kommon.string.Uri
import com.lightningkite.koolui.ExternalAccess.contentTypeToExtensionFilter
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import java.awt.Desktop
import java.net.URI

actual object ExternalAccess {

    actual fun openUri(uri: Uri) {
        try {
            Desktop.getDesktop().browse(URI.create(uri.string))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun saveToChosenFile(
            name: String,
            contentType: MediaTypeWithDescription,
            data: ByteArray,
            alwaysOpenDialog: Boolean,
            callback: (succeeded: Boolean) -> Unit
    ) {
        val chooser = FileChooser()
        chooser.title = "Save"
        chooser.extensionFilters.add(contentType.contentTypeToExtensionFilter())
        chooser.initialFileName = name
        val file = chooser.showSaveDialog(ApplicationAccess.stage)
        if(file == null){
            callback(false)
            return
        }
        file.writeBytes(data)
        callback(true)
    }

    actual fun loadFromChosenFile(contentTypes: List<MediaTypeAcceptWithDescription>, callback: (data: ByteArray?) -> Unit) {
        val chooser = FileChooser()
        chooser.title = "Open"
        chooser.extensionFilters.addAll(contentTypes.map { it.contentTypeToExtensionFilter() })
        val file = chooser.showOpenDialog(ApplicationAccess.stage)
        if(file == null){
            callback(null)
            return
        }
        callback(file.readBytes())
    }

    fun MediaTypeAcceptWithDescription.contentTypeToExtensionFilter(): FileChooser.ExtensionFilter {
        return FileChooser.ExtensionFilter(
                description,
                extensions.map { "*.$it" }
        )
    }

}