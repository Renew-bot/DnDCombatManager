package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dndcombatmanager.combat.platform.imageDropTarget
import com.example.dndcombatmanager.combat.platform.imagePortraitPickingSupported
import com.example.dndcombatmanager.combat.platform.pickImageBytes
import com.example.dndcombatmanager.combat.theme.oklch
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.jetbrains.compose.resources.decodeToImageBitmap

@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.toPortraitString(): String = Base64.Default.encode(this)

@OptIn(ExperimentalEncodingApi::class)
private fun String.decodePortraitBytes(): ByteArray? = runCatching { Base64.Default.decode(this) }.getOrNull()

@Composable
private fun rememberPortraitBitmap(portrait: String?): ImageBitmap? = remember(portrait) {
    portrait?.decodePortraitBytes()?.let { runCatching { it.decodeToImageBitmap() }.getOrNull() }
}

/**
 * Fixed-size square portrait: click or OS drag-and-drop to set an image (desktop only for now),
 * a small badge to clear it once set. [portrait] is the Base64-encoded image, or null.
 */
@Composable
fun PortraitBox(
    portrait: String?,
    onImageBytes: (ByteArray) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    val bitmap = rememberPortraitBitmap(portrait)
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Outer box is unclipped so the clear badge can sit outside the portrait's bounds.
    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(oklch(0.19f, 0.02f, 55f))
                .border(BorderStroke(1.dp, oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(10.dp))
                .hoverable(interactionSource)
                .imageDropTarget(onImageBytes = onImageBytes)
                .let { m ->
                    if (imagePortraitPickingSupported) {
                        m.clickable { scope.launch { pickImageBytes()?.let(onImageBytes) } }
                    } else m
                },
            contentAlignment = Alignment.Center,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap, contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(Icons.Default.Person, contentDescription = null, tint = oklch(0.42f, 0.02f, 70f), modifier = Modifier.size(size * 0.4f))
            }
        }
        if (bitmap != null && isHovered) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(16.dp)
                    .background(oklch(0.21f, 0.02f, 55f), RoundedCornerShape(999.dp))
                    .border(BorderStroke(1.dp, oklch(0.34f, 0.02f, 55f)), RoundedCornerShape(999.dp))
                    .clickable { onClear() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = oklch(0.65f, 0.14f, 25f), modifier = Modifier.size(10.dp))
            }
        }
    }
}
