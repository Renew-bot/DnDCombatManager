package com.example.dndcombatmanager.combat.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionDataDelegateProtocol
import platform.Foundation.NSURLSessionDataTask
import platform.Foundation.NSURLSessionTask
import platform.Foundation.setValue
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import platform.posix.memcpy
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    val out = ByteArray(size)
    out.usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, length) }
    return out
}

/**
 * NSURLSession's completion-handler-based `dataTaskWithRequest(request:completionHandler:)` isn't visible
 * through this Kotlin/Native Foundation binding (only the delegate-driven single-arg overload resolves),
 * so the response is instead collected chunk by chunk through a one-shot [NSURLSessionDataDelegateProtocol]
 * and decoded as UTF-8 once the task completes.
 */
@OptIn(ExperimentalForeignApi::class)
private class DataTaskDelegate(private val cont: CancellableContinuation<String?>) : NSObject(), NSURLSessionDataDelegateProtocol {
    private val chunks = mutableListOf<ByteArray>()

    override fun URLSession(session: NSURLSession, dataTask: NSURLSessionDataTask, didReceiveData: NSData) {
        chunks += didReceiveData.toByteArray()
    }

    override fun URLSession(session: NSURLSession, task: NSURLSessionTask, didCompleteWithError: NSError?) {
        if (didCompleteWithError != null || chunks.isEmpty()) {
            cont.resume(if (didCompleteWithError == null) "" else null)
        } else {
            val total = ByteArray(chunks.sumOf { it.size })
            var offset = 0
            chunks.forEach { chunk -> chunk.copyInto(total, offset); offset += chunk.size }
            cont.resume(total.decodeToString())
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun httpGetText(url: String): String? = suspendCancellableCoroutine { cont ->
    val nsUrl = NSURL.URLWithString(url)
    if (nsUrl == null) {
        cont.resume(null)
        return@suspendCancellableCoroutine
    }
    val request = NSMutableURLRequest(nsUrl)
    request.setValue("application/vnd.github+json", forHTTPHeaderField = "Accept")
    request.setValue("DnDCombatManager", forHTTPHeaderField = "User-Agent")

    val session = NSURLSession.sessionWithConfiguration(
        configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
        delegate = DataTaskDelegate(cont),
        delegateQueue = null,
    )
    val task = session.dataTaskWithRequest(request)
    cont.invokeOnCancellation { task.cancel() }
    task.resume()
}

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}
