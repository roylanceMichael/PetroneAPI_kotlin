package kr.co.byrobot.petroneapi.Packet

/**
 * Created by byrobot on 2017. 6. 27..
 */
class PetroneEXPacket {
    var port : Int = 0
    var data : ByteArray? = null

    constructor( port: Int, data: ByteArray? = null ) {
        this.port = port
        this.data = data
    }
}