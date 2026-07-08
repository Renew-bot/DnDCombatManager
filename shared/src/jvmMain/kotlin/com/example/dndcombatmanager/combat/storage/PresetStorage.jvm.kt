package com.example.dndcombatmanager.combat.storage

import kotlinx.serialization.json.Json
import java.io.File

actual object PresetStorage {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val file: File by lazy {
        val dir = File(System.getProperty("user.home"), ".dndcombatmanager")
        dir.mkdirs()
        File(dir, "presets.json")
    }

    actual fun load(): SavedPresets {
        if (!file.exists()) return SavedPresets()
        return runCatching { json.decodeFromString<SavedPresets>(file.readText()) }.getOrDefault(SavedPresets())
    }

    actual fun save(data: SavedPresets) {
        runCatching { file.writeText(json.encodeToString(data)) }
    }
}
