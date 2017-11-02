package kr.co.byrobot.petroneapi

/**
 * Created by byrobot on 2017. 6. 22..
 */
interface PetroneConnectionCallback {
    fun onConnected()
    fun onDisconnected()
    fun onRecvPacket(data:ByteArray)
    fun onError(error: String)
}