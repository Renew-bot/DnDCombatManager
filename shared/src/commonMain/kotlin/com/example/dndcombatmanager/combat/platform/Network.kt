package com.example.dndcombatmanager.combat.platform

/** GETs [url] and returns the response body as text, or null on any failure (offline, timeout, non-2xx…). */
expect suspend fun httpGetText(url: String): String?

/** Opens [url] in the platform's default/system browser. */
expect fun openUrl(url: String)
