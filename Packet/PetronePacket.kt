package kr.co.byrobot.openapi.Packet

/**
 * Created by byrobot on 2017. 6. 26..
 */

interface PetronePacket {
    val size: Int
    val index: Int

    fun decode(data: PetroneByteArray) : Boolean
    fun encode() : PetroneByteArray
    fun encodeSerial() : PetroneByteArray
}