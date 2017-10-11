package kr.co.byrobot.petroneapi.Packet.Request

import kr.co.byrobot.petroneapi.Enum.PetroneDataType
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray
import kr.co.byrobot.petroneapi.Packet.PetronePacket

/**
 * Created by byrobot on 2017. 9. 26..
 */

abstract class PetronePacketRequest : PetronePacket {
    override var size: Int = 1
    protected var requestType: PetroneDataType = PetroneDataType.None;

    constructor() {
    }

    constructor(requestType: PetroneDataType) {
        this.requestType = requestType
    }

    override fun decode(data: PetroneByteArray) : Boolean{
        return true
    }

    override fun encode() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 1);
        data.set(value = PetroneDataType.Request);
        data.set(value = requestType);
        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 2);
        data.set(value = PetroneDataType.Request);
        data.set(value = size.toByte());
        data.set(value =  requestType);
        return data
    }
}