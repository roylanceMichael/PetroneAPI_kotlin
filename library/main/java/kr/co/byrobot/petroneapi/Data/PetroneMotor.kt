package kr.co.byrobot.petroneapi.Data

import kr.co.byrobot.petroneapi.Packet.PetroneByteArray

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetroneMotor {
    class PetroneMotorBase {
        var  forward:Short = 0
        var  reverse:Short = 0

        fun parse(data:PetroneByteArray) {
            this.forward = data.get16()
            this.reverse = data.get16()
        }
    }
    var  motors:Array<PetroneMotorBase> = arrayOf(PetroneMotorBase(),PetroneMotorBase(),PetroneMotorBase(),PetroneMotorBase())

    fun parse(data:ByteArray) {
        val parse = PetroneByteArray(data)
        parse.setCursor(1)

        this.motors[0].parse(parse)
        this.motors[1].parse(parse)
        this.motors[2].parse(parse)
        this.motors[3].parse(parse)
    }
}