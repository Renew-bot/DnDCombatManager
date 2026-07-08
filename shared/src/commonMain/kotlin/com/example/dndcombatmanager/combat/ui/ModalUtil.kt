package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/** Consumes taps without a ripple, so clicking a modal panel doesn't fall through to the backdrop dismiss handler. */
@Composable
fun Modifier.consumeClicks(): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(interactionSource = interactionSource, indication = null) {}
}
