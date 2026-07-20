package com.example.dndcombatmanager.combat.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.net.HttpURLConnection
import java.net.URI

actual suspend fun httpGetText(url: String): String? = withContext(Dispatchers.IO) {
    runCatching {
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.setRequestProperty("Accept", "application/vnd.github+json")
        connection.setRequestProperty("User-Agent", "DnDCombatManager")
        connection.inputStream.bufferedReader().use { it.readText() }
    }.getOrNull()
}

actual fun openUrl(url: String) {
    runCatching {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            // Desktop.browse is commonly unsupported on Linux depending on the desktop environment; xdg-open covers it.
            ProcessBuilder("xdg-open", url).start()
        }
    }
}
