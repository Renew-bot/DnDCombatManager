package com.example.dndcombatmanager.combat.platform

import android.content.Intent
import android.net.Uri
import com.example.dndcombatmanager.combat.storage.AndroidStorageContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        AndroidStorageContext.appContext.startActivity(intent)
    }
}
