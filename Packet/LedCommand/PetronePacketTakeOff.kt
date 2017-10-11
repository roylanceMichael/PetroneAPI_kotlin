package kr.co.byrobot.openapi.Packet.LedCommand

import kr.co.byrobot.openapi.Enum.*

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketTakeOff : PetronePacketLedCommand(PetroneCommand.FlightEvent) {
    init {
        lightColor = PetroneColors.Red
        option = PetroneFlightEvent.TakeOff.event
    }
}