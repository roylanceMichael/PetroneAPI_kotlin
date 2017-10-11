package kr.co.byrobot.petroneapi.Data

import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetroneCountFlight {
    var time:Long = 0
    var takeOff:Short = 0
    var landing:Short = 0
    var accident:Short = 0
    fun parse(data:ByteArray) {
        val parse = PetroneByteArray(data)
        parse.setCursor(1)

        this.time = parse.get64()
        this.takeOff = parse.get16()
        this.landing = parse.get16()
        this.accident = parse.get16()
    }
}