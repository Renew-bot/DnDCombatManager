package com.example.dndcombatmanager.combat.theme

import androidx.compose.ui.text.font.FontFamily

/**
 * The original design pulls Cinzel / Archivo / IBM Plex Mono from Google Fonts.
 * Custom web fonts aren't bundled here, so we map each role to the closest
 * platform-default family and preserve the original weight/letter-spacing choices.
 */
object Fonts {
    val display: FontFamily = FontFamily.Serif // stands in for Cinzel
    val body: FontFamily = FontFamily.Default // stands in for Archivo
    val mono: FontFamily = FontFamily.Monospace // stands in for IBM Plex Mono
}
