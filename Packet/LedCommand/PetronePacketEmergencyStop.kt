package kr.co.byrobot.openapi.Packet.LedCommand

import kr.co.byrobot.openapi.Enum.PetroneColors
import kr.co.byrobot.openapi.Enum.PetroneCommand
import kr.co.byrobot.openapi.Enum.PetroneFlightEvent

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketEmergencyStop : PetronePacketLedCommand( PetroneCommand.FlightEvent ){
    init {
        lightColor = PetroneColors.Red
        option = PetroneFlightEvent.Stop.event
    }
}