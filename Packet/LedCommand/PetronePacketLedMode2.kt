package kr.co.byrobot.openapi.Packet.LedCommand

import kr.co.byrobot.openapi.Enum.PetroneDataType
import kr.co.byrobot.openapi.Packet.PetroneByteArray
import kr.co.byrobot.openapi.Packet.PetronePacket

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketLedMode2 : PetronePacket {
    override val size: Int = 6
    override val index: Int = 0

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