package kr.co.byrobot.petroneapi.Wifi

import java.io.*
import java.net.*
import kotlinx.coroutines.experimental.*
import kr.co.byrobot.petroneapi.Wifi.channels.*

/**
 * Created by byrobot on 2016. 10. 14..
 */

interface BaseSocket : Closeable, DisposableHandle {
    override fun dispose() {
        try {
            close()
        } catch (ignore: Throwable) {

        }
    }
}

interface BaseConnectedSocket : WriteChannel {
    val remoteAddress: SocketAddress
}

interface BaseBoundSocket {
    val localAddress : SocketAddress
}

interface Acceptable<out S: BaseSocket> : BaseSocket {
    suspend  fun accept(): S
}

interface ReadWriteSocket : BaseSocket, ReadWriteChannel
interface Socket : ReadWriteSocket, BaseBoundSocket, BaseConnectedSocket
