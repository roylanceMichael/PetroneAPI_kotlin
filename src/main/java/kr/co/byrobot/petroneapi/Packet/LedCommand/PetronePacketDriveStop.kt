package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.*

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketDriveStop : PetronePacketLedCommand( PetroneCommand.DriveEvent ){
    init {
        lightColor = PetroneColors.Blue
        option = PetroneDriveEvent.Stop.event
    }
}