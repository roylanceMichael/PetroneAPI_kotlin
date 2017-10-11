package kr.co.byrobot.petroneapi

/**
 * Created by byrobot on 2017. 9. 21..
 */
class PetroneDevice {
    var isFPV : Boolean = false
    var ssid : String = ""
    var name : String = ""
    var rssi : Int = 0

    constructor() {}

    constructor(fpv:Boolean) {
        isFPV = fpv
        ssid = "FPV"
        name = "PETRONE FPV"
    }

    constructor(ssid:String, name:String, rssi:Int) {
        this.ssid = ssid
        this.name = name
        this.rssi = rssi
    }
}