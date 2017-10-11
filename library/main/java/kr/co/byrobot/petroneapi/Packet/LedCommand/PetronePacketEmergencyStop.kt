package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.PetroneColors
import kr.co.byrobot.petroneapi.Enum.PetroneCommand
import kr.co.byrobot.petroneapi.Enum.PetroneFlightEvent

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketEmergencyStop : PetronePacketLedCommand( PetroneCommand.FlightEvent ){
    init {
        lightColor = PetroneColors.Red
        option = PetroneFlightEvent.Stop.event
    }
}