package com.example.dndcombatmanager.combat.state

import com.example.dndcombatmanager.combat.i18n.Language
import com.example.dndcombatmanager.combat.i18n.strings

enum class Layout { SIDEBAR, TIMELINE, FOCUS }

fun Layout.label(lang: Language): String {
    val s = lang.strings()
    return when (this) {
        Layout.SIDEBAR -> s.layoutSidebar
        Layout.TIMELINE -> s.layoutTimeline
        Layout.FOCUS -> s.layoutFocus
    }
}

enum class ResourceKey { ACTION, BONUS, REACTION }
