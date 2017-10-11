package kr.co.byrobot.openapi.Data

import kr.co.byrobot.openapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */

class PetroneTrim {
    var flight: PetroneTrimFlight = PetroneTrimFlight()
    var drive: PetroneTrimDrive = PetroneTrimDrive()

    public fun parse( data:ByteArray) {
        val parse = PetroneByteArray(data)
        parse.setCursor(1)

        this.flight.throttle = parse.get16()
        this.flight.yaw = parse.get16()
        this.flight.roll = parse.get16()
        this.flight.pitch = parse.get16()
        this.drive.wheel = parse.get16()
    }
}