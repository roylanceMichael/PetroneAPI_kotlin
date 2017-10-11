package kr.co.byrobot.petroneapi.Packet

import kr.co.byrobot.petroneapi.Enum.PetroneDataType

/**
 * Created by byrobot on 2017. 6. 26..
 */

class PacketControl : PetronePacket {
    override var size: Int = 4

    var throttle: Byte  = 0
    var yaw: Byte       = 0
    var pitch: Byte     = 0
    var roll: Byte      = 0

    constructor(throttle: Byte, yaw: Byte, pitch: Byte, roll: Byte) {
        this.throttle = throttle
        this.yaw = yaw
        this.pitch = pitch
        this.roll = roll
    }

    constructor(throttle: Int, yaw: Int, pitch: Int, roll: Int) {
        this.throttle = throttle.toByte()
        this.yaw = yaw.toByte()
        this.pitch = pitch.toByte()
        this.roll = roll.toByte()
    }

    override fun decode(data: PetroneByteArray) : Boolean{
        return true
    }

    override fun encode() : PetroneByteArray{
        var data : PetroneByteArray = PetroneByteArray(size+1);
        data.set(value = PetroneDataType.Control);
        data.set(value = throttle);
        data.set(value = yaw);
        data.set(value = pitch);
        data.set(value = roll);

        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size+2);
        data.set(value = PetroneDataType.Control);
        data.set(value = size.toByte());
        data.set(value = throttle);
        data.set(value = yaw);
        data.set(value = pitch);
        data.set(value = roll);
        return data
    }
}