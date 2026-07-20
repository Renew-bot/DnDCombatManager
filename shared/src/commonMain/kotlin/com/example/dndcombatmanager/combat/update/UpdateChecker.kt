package com.example.dndcombatmanager.combat.update

import com.example.dndcombatmanager.combat.APP_VERSION
import com.example.dndcombatmanager.combat.platform.httpGetText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val GITHUB_REPO = "Renew-bot/DnDCombatManager"
private const val LATEST_RELEASE_URL = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"

private val json = Json { ignoreUnknownKeys = true }

@Serializable
private data class GithubRelease(val tag_name: String, val html_url: String)

/** A newer release than the running app's [APP_VERSION], ready to be offered to the user. */
data class UpdateInfo(val version: String, val url: String)

/**
 * Compares two "MAJOR.MINOR.PATCH"-style version strings (an optional leading "v" is ignored,
 * missing/non-numeric segments count as 0). Returns true if [remote] is strictly newer than [current].
 */
internal fun isNewerVersion(current: String, remote: String): Boolean {
    fun parts(v: String) = v.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
    val c = parts(current)
    val r = parts(remote)
    for (i in 0 until maxOf(c.size, r.size)) {
        val cv = c.getOrElse(i) { 0 }
        val rv = r.getOrElse(i) { 0 }
        if (rv != cv) return rv > cv
    }
    return false
}

/** Checks the GitHub repo's latest release; returns null if it's not newer than [APP_VERSION] or the check failed. */
suspend fun checkForUpdate(): UpdateInfo? {
    val body = httpGetText(LATEST_RELEASE_URL) ?: return null
    val release = runCatching { json.decodeFromString<GithubRelease>(body) }.getOrNull() ?: return null
    if (!isNewerVersion(APP_VERSION, release.tag_name)) return null
    return UpdateInfo(version = release.tag_name, url = release.html_url)
}
