package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.*

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketTakeOff : PetronePacketLedCommand(PetroneCommand.FlightEvent) {
    init {
        lightColor = PetroneColors.Red
        option = PetroneFlightEvent.TakeOff.event
    }
}