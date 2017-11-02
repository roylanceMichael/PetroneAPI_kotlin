package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.*
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketRotate180 : PetronePacketLedCommand(PetroneCommand.NONE) {
    var mode:PetroneMode = PetroneMode.NONE

    init {
        lightMode = PetroneLightMode.ArmHold
    }

    override fun encode() : PetroneByteArray {
        if( mode.mode < PetroneMode.DRIVE.mode ) {
            lightColor = PetroneColors.Red
            command = PetroneCommand.FlightEvent
            option = PetroneFlightEvent.Rotate180.event
        } else {
            lightColor = PetroneColors.Blue
            command = PetroneCommand.DriveEvent
            option = PetroneDriveEvent.Rotate180.event
        }

        return super.encode()
    }

    override fun encodeSerial() : PetroneByteArray {
        if( mode.mode < PetroneMode.DRIVE.mode ) {
            lightColor = PetroneColors.Red
            command = PetroneCommand.FlightEvent
            option = PetroneFlightEvent.Rotate180.event
        } else {
            lightColor = PetroneColors.Blue
            command = PetroneCommand.DriveEvent
            option = PetroneDriveEvent.Rotate180.event
        }

        return super.encodeSerial()
    }
}