package com.example.dndcombatmanager.combat.model

import kotlinx.serialization.Serializable

/** A saved snapshot of a whole roster (an encounter), reloadable to start a fresh combat. */
@Serializable
data class CombatPreset(
    val id: String,
    val name: String,
    val characters: List<CharacterPreset>,
)

fun CombatPreset.metaLabel(): String {
    val count = characters.size
    val who = characters.joinToString(", ") { it.name }
    return "$count combattant(s) — $who"
}
