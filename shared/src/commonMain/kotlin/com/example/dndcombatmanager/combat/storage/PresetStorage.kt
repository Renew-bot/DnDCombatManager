package com.example.dndcombatmanager.combat.storage

import com.example.dndcombatmanager.combat.model.CharacterPreset
import com.example.dndcombatmanager.combat.model.CombatPreset
import kotlinx.serialization.Serializable

/** Everything that gets persisted to disk so it survives an app restart. */
@Serializable
data class SavedPresets(
    val presets: List<CharacterPreset> = emptyList(),
    val combatPresets: List<CombatPreset> = emptyList(),
    val language: String = "FR",
)

/** Platform-specific storage for character and combat presets. */
expect object PresetStorage {
    fun load(): SavedPresets
    fun save(data: SavedPresets)
}
