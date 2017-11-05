package kr.co.byrobot.petroneapi.Wifi

import android.annotation.SuppressLint
import kr.co.byrobot.petroneapi.Wifi.Impl.SocketImpl
import kr.co.byrobot.petroneapi.Wifi.selector.SelectorManager
import kr.co.byrobot.petroneapi.Wifi.selector.buildOrClose
import java.net.SocketAddress
import java.net.SocketOption
import java.net.StandardSocketOptions
import java.nio.channels.NetworkChannel
import java.nio.channels.SelectableChannel

class SocketOptions private constructor(private val allOptions: MutableMap<SocketOption<*>, Any?> = HashMap()) {
    internal constructor() : this(HashMap())

    fun copy() = SocketOptions(allOptions.toMutableMap())

    operator fun <T> get(option: SocketOption<T>): T = @Suppress("UNCHECKED_CAST") (allOptions[option] as T)

    operator fun <T> set(option: SocketOption<T>, value: T) {
        allOptions[option] = value
    }

    fun list(): List<Pair<SocketOption<*>, Any?>> = allOptions.entries.map { Pair(it.key, it.value) }

    companion object {
        val Empty = SocketOptions()
    }
}

interface Configurable<out T : Configurable<T>> {
    var options: SocketOptions

    fun configure(block: SocketOptions.() -> Unit): T {
        val newOptions = options.copy()
        block(newOptions)
        options = newOptions

        @Suppress("UNCHECKED_CAST")
        return this as T
    }
}

@SuppressLint("NewApi")
fun <T: Configurable<T>> T.tcpNoDelay(): T {
    return configure {
        this[StandardSocketOptions.TCP_NODELAY] = true
    }
}

fun petroneSocket(selector: SelectorManager = SelectorManager.DefaultSelectorManager) = SocketBuilder(selector, SocketOptions.Empty)

class SocketBuilder internal constructor(val selector: SelectorManager, override var options: SocketOptions) : Configurable<SocketBuilder> {
    fun tcp() = TcpSocketBuilder(selector, options)
}

class TcpSocketBuilder internal constructor(val selector: SelectorManager, override var options: SocketOptions) : Configurable<TcpSocketBuilder> {
    suspend fun connect(remoteAddress: SocketAddress): Socket {
        return selector.buildOrClose({ openSocketChannel() }) {
            assignOptions(options)
            nonBlocking()

            SocketImpl(this, selector).apply {
                connect(remoteAddress)
            }
        }
    }
}

private fun NetworkChannel.assignOptions(options: SocketOptions) {
    options.list().forEach { (k, v) ->
        @Suppress("UNCHECKED_CAST")
        (setOption(k as SocketOption<Any?>, v))
    }
}

private fun SelectableChannel.nonBlocking() {
    configureBlocking(false)
}