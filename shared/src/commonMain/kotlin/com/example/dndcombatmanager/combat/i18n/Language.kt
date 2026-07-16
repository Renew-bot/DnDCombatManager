package com.example.dndcombatmanager.combat.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

enum class Language { FR, EN }

val LocalLanguage = compositionLocalOf { Language.FR }

@Composable
fun strings(): Strings = LocalLanguage.current.strings()
