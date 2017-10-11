package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.PetroneColors
import kr.co.byrobot.petroneapi.Enum.PetroneCommand
import kr.co.byrobot.petroneapi.Enum.PetroneDataType
import kr.co.byrobot.petroneapi.Enum.PetroneLightMode
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray
import kr.co.byrobot.petroneapi.Packet.PetronePacket

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketLedMode : PetronePacket {
    override var size: Int = 3

    var led: PetroneLedModeBase = PetroneLedModeBase()

    override fun decode(data: PetroneByteArray) : Boolean{
        return true
    }

    override fun encode() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 1)
        data.set(value = PetroneDataType.LedMode)
        data.set(value = led.mode)
        data.set(value = led.color)
        data.set(value = led.interval)

        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 2);
        data.set(value = PetroneDataType.LedMode)
        data.set(value = size)
        data.set(value = led.mode)
        data.set(value = led.color)
        data.set(value = led.interval)

        return data
    }
}