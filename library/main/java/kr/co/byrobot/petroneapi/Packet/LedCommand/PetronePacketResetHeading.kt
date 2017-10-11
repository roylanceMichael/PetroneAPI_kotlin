package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.*
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

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