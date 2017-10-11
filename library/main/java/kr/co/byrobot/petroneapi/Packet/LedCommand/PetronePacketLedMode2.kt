package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.PetroneDataType
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray
import kr.co.byrobot.petroneapi.Packet.PetronePacket

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketLedMode2 : PetronePacket {
    override var size: Int = 6

    var led1: PetroneLedModeBase = PetroneLedModeBase()
    var led2: PetroneLedModeBase = PetroneLedModeBase()

    override fun decode(data: PetroneByteArray) : Boolean{
        return true
    }

    override fun encode() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 1)
        data.set(value = PetroneDataType.LedMode2)
        data.set(value = led1.mode)
        data.set(value = led1.color)
        data.set(value = led1.interval)
        data.set(value = led2.mode)
        data.set(value = led2.color)
        data.set(value = led2.interval)

        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 2);
        data.set(value = PetroneDataType.LedMode2)
        data.set(value = size)
        data.set(value = led1.mode)
        data.set(value = led1.color)
        data.set(value = led1.interval)
        data.set(value = led2.mode)
        data.set(value = led2.color)
        data.set(value = led2.interval)

        return data
    }
}