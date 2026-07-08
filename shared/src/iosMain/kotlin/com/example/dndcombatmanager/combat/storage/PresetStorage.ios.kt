package com.example.dndcombatmanager.combat.storage

import kotlinx.serialization.json.Json
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

actual object PresetStorage {
    private val json = Json { ignoreUnknownKeys = true }

    private val filePath: String by lazy {
        val documentsDir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            .firstOrNull() as? String
        "${documentsDir ?: "."}/presets.json"
    }

    actual fun load(): SavedPresets {
        val content = runCatching {
            NSString.stringWithContentsOfFile(filePath, encoding = NSUTF8StringEncoding, error = null)
        }.getOrNull() as? String ?: return SavedPresets()
        return runCatching { json.decodeFromString<SavedPresets>(content) }.getOrDefault(SavedPresets())
    }

    actual fun save(data: SavedPresets) {
        val content = json.encodeToString(data)
        runCatching { content.writeToFile(filePath, atomically = true, encoding = NSUTF8StringEncoding, error = null) }
    }
}
