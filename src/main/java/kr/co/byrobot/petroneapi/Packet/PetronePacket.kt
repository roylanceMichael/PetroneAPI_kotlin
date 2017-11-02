package kr.co.byrobot.petroneapi.Packet

/**
 * Created by byrobot on 2017. 6. 26..
 */

interface PetronePacket {
    var size: Int

    fun decode(data: PetroneByteArray) : Boolean
    fun encode() : PetroneByteArray
    fun encodeSerial() : PetroneByteArray
}