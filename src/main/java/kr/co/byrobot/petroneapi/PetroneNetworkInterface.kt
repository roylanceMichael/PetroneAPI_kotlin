package kr.co.byrobot.petroneapi

import kr.co.byrobot.petroneapi.Packet.PetronePacket
import java.util.*

/**
 * Created by byrobot on 2017. 6. 22..
 */
interface PetroneNetworkInterface {
    fun initWithCallback(callback: PetroneCallback)
    fun scan(onScan: (ssid:String, name:String, rssi:Int) -> Unit, onScanFailure : (error:String) -> Unit)
    fun stopScan()
    fun connect(target: String, callback: PetroneConnectionCallback)
    fun isConnected(): Boolean
    fun getConnectionList(): List<String>
    fun disconnect(target: String = "")
    fun sendPacket(packet: PetronePacket)
    fun recvPacket(data: ByteArray)
}