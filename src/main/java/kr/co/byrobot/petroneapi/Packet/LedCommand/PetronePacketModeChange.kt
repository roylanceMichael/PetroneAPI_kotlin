package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.*
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketModeChange : PetronePacketLedCommand(PetroneCommand.ModePetrone) {
     var mode:PetroneMode = PetroneMode.NONE

    init {
        lightMode = PetroneLightMode.ArmFlicker
        interval = 100
        repeatCount = 2
    }

    override fun encode() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 1)
        data.set(value = PetroneDataType.LedEventCommand)
        data.set(value = lightMode.mode)

        if( mode.mode < PetroneMode.DRIVE.mode ) {
            data.set(value = PetroneColors.Red.color)
        } else {
            data.set(value = PetroneColors.Blue.color)
        }

        data.set(value = interval)
        data.set(value = repeatCount)
        data.set(value = command.command)
        data.set(value = mode.mode)

        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 2);
        data.set(value = PetroneDataType.LedEventCommand)
        data.set(value = size)
        data.set(value = lightMode.mode)

        if( mode.mode < PetroneMode.DRIVE.mode ) {
            data.set(value = PetroneColors.Red.color)
        } else {
            data.set(value = PetroneColors.Blue.color)
        }

        data.set(value = interval)
        data.set(value = repeatCount)
        data.set(value = command.command)
        data.set(value = mode.mode)

        return data
    }
}