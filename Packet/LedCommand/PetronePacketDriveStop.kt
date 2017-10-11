package kr.co.byrobot.openapi.Packet.LedCommand

import kr.co.byrobot.openapi.Enum.*

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketDriveStop : PetronePacketLedCommand( PetroneCommand.DriveEvent ){
    init {
        lightColor = PetroneColors.Blue
        option = PetroneDriveEvent.Stop.event
    }
}