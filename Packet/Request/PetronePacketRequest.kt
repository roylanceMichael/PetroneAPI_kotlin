package kr.co.byrobot.openapi.Packet.Request

import kr.co.byrobot.openapi.Enum.PetroneDataType
import kr.co.byrobot.openapi.Packet.PetroneByteArray
import kr.co.byrobot.openapi.Packet.PetronePacket

/**
 * Created by byrobot on 2017. 9. 26..
 */

abstract class PetronePacketRequest : PetronePacket {
    override val size: Int = 1
    override val index: Int = 0
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