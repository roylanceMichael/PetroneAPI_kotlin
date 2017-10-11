package kr.co.byrobot.openapi.Data

import kr.co.byrobot.openapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetroneTrimDrive {
    var wheel: Short = 0
    fun parse(data:ByteArray) {
        val parse = PetroneByteArray(data)

        parse.setCursor(1)
        this.wheel = parse.get16()
    }
}