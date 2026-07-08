package com.example.dndcombatmanager.combat.storage

import android.content.Context
import kotlinx.serialization.json.Json
import java.io.File

/** Set once from the Android `Application.onCreate()` so [PresetStorage] can reach app-private storage. */
object AndroidStorageContext {
    lateinit var appContext: Context
}

actual object PresetStorage {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val file: File by lazy {
        File(AndroidStorageContext.appContext.filesDir, "presets.json")
    }

    actual fun load(): SavedPresets {
        if (!file.exists()) return SavedPresets()
        return runCatching { json.decodeFromString<SavedPresets>(file.readText()) }.getOrDefault(SavedPresets())
    }

    actual fun save(data: SavedPresets) {
        runCatching { file.writeText(json.encodeToString(data)) }
    }
}
