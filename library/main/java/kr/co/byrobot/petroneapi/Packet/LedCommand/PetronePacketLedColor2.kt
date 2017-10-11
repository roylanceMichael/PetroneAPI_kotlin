package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Data.PetroneLedBase
import kr.co.byrobot.petroneapi.Enum.PetroneDataType
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray
import kr.co.byrobot.petroneapi.Packet.PetronePacket

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketLedColor2 : PetronePacket {
    override var size: Int = 10

    var led1: PetroneLedBase = PetroneLedBase()
    var led2: PetroneLedBase = PetroneLedBase()

    override fun decode(data: PetroneByteArray) : Boolean{
        return true
    }

    override fun encode() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 1)
        data.set(value = PetroneDataType.LedModeColor)
        data.set(value = led1.mode)
        data.set(value = led1.red)
        data.set(value = led1.green)
        data.set(value = led1.blue)
        data.set(value = led1.interval)
        data.set(value = led2.mode)
        data.set(value = led2.red)
        data.set(value = led2.green)
        data.set(value = led2.blue)
        data.set(value = led2.interval)

        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 2);
        data.set(value = PetroneDataType.LedModeColor)
        data.set(value = size)
        data.set(value = led1.mode)
        data.set(value = led1.red)
        data.set(value = led1.green)
        data.set(value = led1.blue)
        data.set(value = led1.interval)
        data.set(value = led2.mode)
        data.set(value = led2.red)
        data.set(value = led2.green)
        data.set(value = led2.blue)
        data.set(value = led2.interval)

        return data
    }
}