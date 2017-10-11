package kr.co.byrobot.openapi.Data

import kr.co.byrobot.openapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetroneCountDrive {
    var time:Long = 0
    var accident:Short = 0
    fun parse(data:ByteArray) {
        val parse = PetroneByteArray(data)
        parse.setCursor(1)
        this.time = parse.get64()
        this.accident = parse.get16()
    }
}