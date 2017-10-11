package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.*
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketCoordinatesChange : PetronePacketLedCommand(PetroneCommand.Coordinate) {
    var coordinates:PetroneCoordinate = PetroneCoordinate.NONE

    init {
        lightMode = PetroneLightMode.ArmFlicker
        lightColor = PetroneColors.Green
        interval = 100
        repeatCount = 3
    }

    override fun encode() : PetroneByteArray {
        option = coordinates.status

        return super.encode()
    }

    override fun encodeSerial() : PetroneByteArray {
        option = coordinates.status

        return super.encodeSerial()
    }
}