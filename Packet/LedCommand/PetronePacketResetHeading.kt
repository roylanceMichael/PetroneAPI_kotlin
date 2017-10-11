package kr.co.byrobot.openapi.Packet.LedCommand

import kr.co.byrobot.openapi.Enum.*
import kr.co.byrobot.openapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketResetHeading : PetronePacketLedCommand(PetroneCommand.ResetHeading) {
    init {
        lightMode = PetroneLightMode.ArmFlickerDouble
        lightColor = PetroneColors.Violet
        interval = 200.toByte()
        repeatCount = 2
    }
}