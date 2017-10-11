package kr.co.byrobot.openapi.Packet

import kr.co.byrobot.openapi.Enum.PetroneDataType

/**
 * Created by byrobot on 2017. 6. 26..
 */

class PacketControl : PetronePacket {
    override val size: Int = 4
    override val index: Int = 0

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

    override fun decode(data: PetroneByteArray) : Boolean{
        return true
    }

    override fun encode() : PetroneByteArray{
        var data : PetroneByteArray = PetroneByteArray(size+1);
        data.set(index, PetroneDataType.Control);
        data.set(+index, throttle);
        data.set(+index, yaw);
        data.set(+index, pitch);
        data.set(+index, roll);

        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size+2);
        data.set(index, PetroneDataType.Control);
        data.set(+index, size.toByte());
        data.set(+index, throttle);
        data.set(+index, yaw);
        data.set(+index, pitch);
        data.set(+index, roll);
        return data
    }
}