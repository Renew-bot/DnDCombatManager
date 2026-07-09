package com.example.dndcombatmanager.combat.platform

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import java.io.File
import java.net.URI
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual val imagePortraitPickingSupported: Boolean = true

actual suspend fun pickImageBytes(): ByteArray? {
    val chooser = JFileChooser().apply {
        fileFilter = FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "webp", "bmp", "gif")
    }
    if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return null
    return runCatching { chooser.selectedFile.readBytes() }.getOrNull()
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.imageDropTarget(onImageBytes: (ByteArray) -> Unit): Modifier =
    this.dragAndDropTarget(
        shouldStartDragAndDrop = { true },
        target = object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val files = (event.dragData() as? DragData.FilesList)?.readFiles() ?: return false
                val uri = files.firstOrNull() ?: return false
                val bytes = runCatching { File(URI(uri)).readBytes() }.getOrNull() ?: return false
                onImageBytes(bytes)
                return true
            }
        },
    )
