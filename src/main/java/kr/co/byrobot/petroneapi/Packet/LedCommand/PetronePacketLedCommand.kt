package kr.co.byrobot.petroneapi.Packet.LedCommand
import kr.co.byrobot.petroneapi.Enum.*

import kr.co.byrobot.petroneapi.Packet.PetroneByteArray
import kr.co.byrobot.petroneapi.Packet.PetronePacket

/**
 * Created by byrobot on 2017. 9. 26..
 */
abstract class PetronePacketLedCommand  : PetronePacket {
    override var size: Int = 6

    var lightMode:PetroneLightMode = PetroneLightMode.ArmHold
    var lightColor:PetroneColors = PetroneColors.Red
    var interval:Byte = 0
    var repeatCount:Byte = 0
    var command:PetroneCommand = PetroneCommand.NONE
    var option:Byte = 0

    constructor() {
    }

    constructor(command: PetroneCommand) {
        this.command = command
    }

    override fun decode(data: PetroneByteArray) : Boolean{
        return true
    }

    override fun encode() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 1)
        data.set(value = PetroneDataType.LedEventCommand)
        data.set(value = lightMode.mode)
        data.set(value = lightColor.color)
        data.set(value = interval)
        data.set(value = repeatCount)
        data.set(value = command.command)
        data.set(value = option)

        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 2);
        data.set(value = PetroneDataType.LedEventCommand)
        data.set(value = size)
        data.set(value = lightMode.mode)
        data.set(value = lightColor.color)
        data.set(value = interval)
        data.set(value = repeatCount)
        data.set(value = command.command)
        data.set(value = option)

        return data
    }
}