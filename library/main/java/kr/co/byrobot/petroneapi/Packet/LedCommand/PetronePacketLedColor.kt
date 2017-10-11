package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Data.PetroneLedBase
import kr.co.byrobot.petroneapi.Enum.PetroneDataType
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray
import kr.co.byrobot.petroneapi.Packet.PetronePacket

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketLedColor : PetronePacket {
    override var size: Int = 5

    var led: PetroneLedBase = PetroneLedBase()

    override fun decode(data: PetroneByteArray) : Boolean{
        return true
    }

    override fun encode() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 1)
        data.set(value = PetroneDataType.LedModeColor)
        data.set(value = led.mode)
        data.set(value = led.red)
        data.set(value = led.green)
        data.set(value = led.blue)
        data.set(value = led.interval)

        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 2);
        data.set(value = PetroneDataType.LedModeColor)
        data.set(value = size)
        data.set(value = led.mode)
        data.set(value = led.red)
        data.set(value = led.green)
        data.set(value = led.blue)
        data.set(value = led.interval)

        return data
    }
}