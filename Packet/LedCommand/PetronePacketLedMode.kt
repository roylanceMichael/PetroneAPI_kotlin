package kr.co.byrobot.openapi.Packet.LedCommand

import kr.co.byrobot.openapi.Enum.PetroneColors
import kr.co.byrobot.openapi.Enum.PetroneCommand
import kr.co.byrobot.openapi.Enum.PetroneDataType
import kr.co.byrobot.openapi.Enum.PetroneLightMode
import kr.co.byrobot.openapi.Packet.PetroneByteArray
import kr.co.byrobot.openapi.Packet.PetronePacket

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketLedMode : PetronePacket {
    override val size: Int = 3
    override val index: Int = 0

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