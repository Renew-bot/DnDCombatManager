package com.example.dndcombatmanager.combat.platform

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.UniformTypeIdentifiers.UTTypePNG
import platform.darwin.NSObject
import platform.posix.memcpy

/** Walks down `presentedViewController` to find the view controller currently on top, to present the picker from. */
private fun topViewController(): UIViewController? {
    var top = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (top?.presentedViewController != null) top = top.presentedViewController
    return top
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    val out = ByteArray(size)
    out.usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, length) }
    return out
}

actual val imagePortraitPickingSupported: Boolean = true

/**
 * Files-based picker (not the Photos picker) so no `NSPhotoLibraryUsageDescription` privacy
 * prompt is needed — the user still reaches their photo library through the Files app's "Browse" source.
 */
actual suspend fun pickImageBytes(): ByteArray? {
    val presenter = topViewController() ?: return null
    val pickedUrl = CompletableDeferred<NSURL?>()

    val picker = UIDocumentPickerViewController(forOpeningContentTypes = listOf(UTTypeImage))
    val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
        override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
            pickedUrl.complete(didPickDocumentsAtURLs.firstOrNull() as? NSURL)
        }

        override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
            pickedUrl.complete(null)
        }
    }
    picker.delegate = delegate
    presenter.presentViewController(picker, animated = true, completion = null)

    val url = pickedUrl.await() ?: return null
    val path = url.path ?: return null
    val hasScope = url.startAccessingSecurityScopedResource()
    return try {
        NSFileManager.defaultManager.contentsAtPath(path)?.toByteArray()
    } finally {
        if (hasScope) url.stopAccessingSecurityScopedResource()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.imageDropTarget(onImageBytes: (ByteArray) -> Unit): Modifier =
    this.dragAndDropTarget(
        shouldStartDragAndDrop = { true },
        target = object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val provider = event.items.firstOrNull()?.itemProvider ?: return false
                if (!provider.hasItemConformingToTypeIdentifier(UTTypePNG.identifier)) return false
                provider.loadDataRepresentationForTypeIdentifier(UTTypePNG.identifier) { data, _ ->
                    val bytes = data?.toByteArray() ?: return@loadDataRepresentationForTypeIdentifier
                    MainScope().launch { onImageBytes(bytes) }
                }
                return true
            }
        },
    )
