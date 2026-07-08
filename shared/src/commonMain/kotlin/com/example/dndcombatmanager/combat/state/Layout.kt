package com.example.dndcombatmanager.combat.state

enum class Layout(val label: String) {
    SIDEBAR("Colonne"),
    TIMELINE("Frise"),
    FOCUS("Focus"),
}

enum class ResourceKey { ACTION, BONUS, REACTION }
