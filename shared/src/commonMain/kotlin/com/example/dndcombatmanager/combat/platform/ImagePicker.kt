package com.example.dndcombatmanager.combat.platform

import androidx.compose.ui.Modifier

/** True on platforms with a working native image picker/drop implementation below. */
expect val imagePortraitPickingSupported: Boolean

/** Opens the platform's native image picker and returns the chosen image's raw bytes, or null if cancelled. */
expect suspend fun pickImageBytes(): ByteArray?

/** Accepts a native OS drag-and-drop of an image file onto this composable, forwarding its raw bytes. No-op where unsupported. */
expect fun Modifier.imageDropTarget(onImageBytes: (ByteArray) -> Unit): Modifier
