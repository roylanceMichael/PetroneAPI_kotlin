package kr.co.byrobot.petroneapi.Wifi

import kotlinx.coroutines.experimental.*
import kr.co.byrobot.petroneapi.Wifi.channels.ReadChannel
import kr.co.byrobot.petroneapi.Wifi.channels.WriteChannel
import kr.co.byrobot.petroneapi.Wifi.selector.Selectable
import java.io.*
import java.nio.*
import java.nio.channels.*

suspend fun ReadChannel.readFully(dst: ByteBuffer) {
    do {
        if (read(dst) == -1) {
            if (dst.hasRemaining()) throw IOException("Unexpected eof")
            break
        }
    } while (dst.hasRemaining())
}

suspend fun WriteChannel.writeFully(src: ByteBuffer) {
    do {
        write(src)
    } while (src.hasRemaining())
}

internal fun CancellableContinuation<*>.disposeOnCancel(disposableHandle: DisposableHandle) {
    invokeOnCompletion { if (isCancelled) disposableHandle.dispose() }
}

internal var SelectionKey.subject: Selectable?
    get() = attachment() as? Selectable
    set(newValue) {
        attach(newValue)
    }