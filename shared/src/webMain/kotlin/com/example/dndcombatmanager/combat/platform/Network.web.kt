package com.example.dndcombatmanager.combat.platform

import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import org.w3c.xhr.XMLHttpRequest

actual suspend fun httpGetText(url: String): String? {
    val deferred = CompletableDeferred<String?>()
    val xhr = XMLHttpRequest()
    xhr.open("GET", url, true)
    xhr.setRequestHeader("Accept", "application/vnd.github+json")
    xhr.onload = { deferred.complete(if (xhr.status.toInt() in 200..299) xhr.responseText else null) }
    xhr.onerror = { deferred.complete(null) }
    xhr.send()
    return deferred.await()
}

actual fun openUrl(url: String) {
    window.open(url, "_blank")
}
