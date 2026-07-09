package com.example.dndcombatmanager.combat.platform

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.domDataTransferOrNull
import kotlinx.browser.document
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.get

actual val imagePortraitPickingSupported: Boolean = true

private suspend fun File.readAllBytes(): ByteArray {
    val deferred = CompletableDeferred<ByteArray>()
    val reader = FileReader()
    reader.onload = {
        val buffer = reader.result!!.unsafeCast<ArrayBuffer>()
        val array = Int8Array(buffer)
        deferred.complete(ByteArray(array.length) { i -> array[i] })
    }
    reader.onerror = { deferred.completeExceptionally(RuntimeException("Échec de lecture du fichier")) }
    reader.readAsArrayBuffer(this)
    return deferred.await()
}

actual suspend fun pickImageBytes(): ByteArray? {
    val picked = CompletableDeferred<File?>()
    val input = (document.createElement("input") as HTMLInputElement).apply {
        type = "file"
        accept = "image/*"
        onchange = { picked.complete(files?.get(0)) }
        oncancel = { picked.complete(null) }
    }
    input.click()
    val file = picked.await() ?: return null
    return file.readAllBytes()
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.imageDropTarget(onImageBytes: (ByteArray) -> Unit): Modifier =
    this.dragAndDropTarget(
        shouldStartDragAndDrop = { true },
        target = object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val file = event.transferData?.domDataTransferOrNull?.files?.get(0) ?: return false
                MainScope().launch { onImageBytes(file.readAllBytes()) }
                return true
            }
        },
    )
