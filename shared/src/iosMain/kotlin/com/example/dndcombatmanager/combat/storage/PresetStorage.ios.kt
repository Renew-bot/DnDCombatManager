package com.example.dndcombatmanager.combat.storage

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.serialization.json.Json
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringWithContentsOfFile
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite

actual object PresetStorage {
    private val json = Json { ignoreUnknownKeys = true }

    private val filePath: String by lazy {
        val documentsDir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            .firstOrNull() as? String
        "${documentsDir ?: "."}/presets.json"
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun load(): SavedPresets {
        val content = runCatching {
            NSString.stringWithContentsOfFile(filePath, encoding = NSUTF8StringEncoding, error = null)
        }.getOrNull() ?: return SavedPresets()
        return runCatching { json.decodeFromString<SavedPresets>(content) }.getOrDefault(SavedPresets())
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun save(data: SavedPresets) {
        // NSString.writeToFile needs a real NSString receiver (a plain Kotlin String can't be cast to
        // one), and NSData's factory methods don't resolve in this toolchain — plain POSIX file I/O
        // sidesteps both and needs no Foundation bridging at all.
        val bytes = json.encodeToString(data).encodeToByteArray()
        runCatching {
            val file = fopen(filePath, "w") ?: return@runCatching
            try {
                if (bytes.isNotEmpty()) {
                    bytes.usePinned { pinned -> fwrite(pinned.addressOf(0), 1u, bytes.size.toULong(), file) }
                }
            } finally {
                fclose(file)
            }
        }
    }
}
