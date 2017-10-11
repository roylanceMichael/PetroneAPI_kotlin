package kr.co.byrobot.openapi

/**
 * Created by byrobot on 2017. 6. 22..
 */
interface PetroneConnectionCallback {
    fun onConnected()
    fun onDisconnected()
    fun onRecvPacket(data:ByteArray)
    fun onError(error: String)
}