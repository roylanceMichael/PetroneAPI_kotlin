package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.*
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketSquare : PetronePacketLedCommand(PetroneCommand.NONE) {
    var mode: PetroneMode = PetroneMode.NONE

    init {
        lightMode = PetroneLightMode.ArmHold
    }

    override fun encode() : PetroneByteArray {
        if( mode.mode < PetroneMode.DRIVE.mode ) {
            lightColor = PetroneColors.Red
            command = PetroneCommand.FlightEvent
            option = PetroneFlightEvent.Square.event
        } else {
            lightColor = PetroneColors.Blue
            command = PetroneCommand.DriveEvent
            option = PetroneDriveEvent.Square.event
        }

        return super.encode()
    }

    override fun encodeSerial() : PetroneByteArray {
        if( mode.mode < PetroneMode.DRIVE.mode ) {
            lightColor = PetroneColors.Red
            command = PetroneCommand.FlightEvent
            option = PetroneFlightEvent.Square.event
        } else {
            lightColor = PetroneColors.Blue
            command = PetroneCommand.DriveEvent
            option = PetroneDriveEvent.Square.event
        }

        return super.encodeSerial()
    }
}