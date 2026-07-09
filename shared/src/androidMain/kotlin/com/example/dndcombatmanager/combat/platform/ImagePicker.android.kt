package com.example.dndcombatmanager.combat.platform

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import com.example.dndcombatmanager.combat.storage.AndroidStorageContext
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Set once from `MainActivity.onCreate()` (it needs a live [androidx.activity.ComponentActivity]
 * to register an [androidx.activity.result.ActivityResultLauncher]), so [pickImageBytes] can reach it.
 */
object AndroidImagePickerBridge {
    var launch: ((onResult: (ByteArray?) -> Unit) -> Unit)? = null
}

actual val imagePortraitPickingSupported: Boolean = true

actual suspend fun pickImageBytes(): ByteArray? = suspendCancellableCoroutine { cont ->
    val launch = AndroidImagePickerBridge.launch
    if (launch == null) {
        cont.resumeWith(Result.success(null))
    } else {
        launch { bytes -> cont.resumeWith(Result.success(bytes)) }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.imageDropTarget(onImageBytes: (ByteArray) -> Unit): Modifier =
    this.dragAndDropTarget(
        shouldStartDragAndDrop = { true },
        target = object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val clipData = event.toAndroidDragEvent().clipData ?: return false
                if (clipData.itemCount == 0) return false
                val uri = clipData.getItemAt(0).uri ?: return false
                val bytes = runCatching {
                    AndroidStorageContext.appContext.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }.getOrNull() ?: return false
                onImageBytes(bytes)
                return true
            }
        },
    )
