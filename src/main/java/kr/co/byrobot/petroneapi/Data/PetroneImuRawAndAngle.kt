package kr.co.byrobot.petroneapi.Data

import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetroneImuRawAndAngle {
    var  accX:Short = 0
    var  accY:Short = 0
    var  accZ:Short = 0
    var  gyroRoll:Short = 0
    var  gyroPitch:Short = 0
    var  gyroYaw:Short = 0
    var  angleRoll:Short = 0
    var  anglePitch:Short = 0
    var  angleYaw:Short = 0

    fun parse(data:ByteArray) {
        val parse = PetroneByteArray(data)
        parse.setCursor(1)
        this.accX = parse.get16()
        this.accY = parse.get16()
        this.accZ = parse.get16()
        this.gyroRoll = parse.get16()
        this.gyroPitch = parse.get16()
        this.gyroYaw = parse.get16()
        this.angleRoll = parse.get16()
        this.anglePitch = parse.get16()
        this.angleYaw = parse.get16()
    }
}