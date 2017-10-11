package kr.co.byrobot.petroneapi.Data

import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetroneImageFlow {
    var fVelocitySumX:Int = 0
    var fVelocitySumY:Int = 0

    fun parse(data:ByteArray) {
        val parse = PetroneByteArray(data)
        parse.setCursor(1)
        this.fVelocitySumX = parse.get32()
        this.fVelocitySumY = parse.get32()
    }
}