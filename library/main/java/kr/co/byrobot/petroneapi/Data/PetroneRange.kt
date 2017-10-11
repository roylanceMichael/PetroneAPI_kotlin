package kr.co.byrobot.petroneapi.Data

import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetroneRange {
    var left:Short = 0
    var front:Short = 0
    var right:Short = 0
    var rear:Short = 0
    var top:Short = 0
    var bottom:Short = 0

    fun parse(data:ByteArray) {
        val parse = PetroneByteArray(data)
        parse.setCursor(1)
        this.left = parse.get16()
        this.front = parse.get16()
        this.right = parse.get16()
        this.rear = parse.get16()
        this.top = parse.get16()
        this.bottom = parse.get16()
    }
}