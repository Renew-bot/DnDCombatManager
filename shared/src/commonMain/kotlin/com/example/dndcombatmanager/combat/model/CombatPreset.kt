package com.example.dndcombatmanager.combat.model

import com.example.dndcombatmanager.combat.i18n.Language
import com.example.dndcombatmanager.combat.i18n.strings
import kotlinx.serialization.Serializable

/** A saved snapshot of a whole roster (an encounter), reloadable to start a fresh combat. */
@Serializable
data class CombatPreset(
    val id: String,
    val name: String,
    val characters: List<CharacterPreset>,
)

fun CombatPreset.metaLabel(lang: Language): String {
    val count = characters.size
    val who = characters.joinToString(", ") { it.name }
    return lang.strings().combatPresetMeta(count, who)
}
