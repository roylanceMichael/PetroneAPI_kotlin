package kr.co.byrobot.openapi.Data

import kr.co.byrobot.openapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetronePressure {
    var  d1:Int = 0
    var  d2:Int = 0
    var  temperature:Int = 0
    var  pressure:Int = 0

    fun parse(data:ByteArray) {
        val parse = PetroneByteArray(data)
        parse.setCursor(1)
        this.d1 = parse.get32()
        this.d2 = parse.get32()
        this.temperature = parse.get32()
        this.pressure = parse.get32()
    }
}