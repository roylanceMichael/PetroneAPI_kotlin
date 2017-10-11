package kr.co.byrobot.petroneapi.Data

import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetroneTrimFlight {
    var throttle: Short = 0
    var yaw: Short = 0
    var roll: Short = 0
    var pitch: Short = 0
    fun parse(data:ByteArray) {
        val parse = PetroneByteArray(data)
        parse.setCursor(1)

        this.throttle = parse.get16()
        this.yaw = parse.get16()
        this.roll = parse.get16()
        this.pitch = parse.get16()
    }
}