package com.example.dndcombatmanager.combat.storage

import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

private const val STORAGE_KEY = "dndcombatmanager_presets"

actual object PresetStorage {
    private val json = Json { ignoreUnknownKeys = true }

    actual fun load(): SavedPresets {
        val raw = localStorage.getItem(STORAGE_KEY) ?: return SavedPresets()
        return runCatching { json.decodeFromString<SavedPresets>(raw) }.getOrDefault(SavedPresets())
    }

    actual fun save(data: SavedPresets) {
        runCatching { localStorage.setItem(STORAGE_KEY, json.encodeToString(data)) }
    }
}
