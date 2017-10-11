package kr.co.byrobot.petroneapi.Packet.LedCommand

import kr.co.byrobot.petroneapi.Enum.PetroneLightMode

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetroneLedModeBase {
    var mode:Byte = PetroneLightMode.NONE.mode
    var color:Byte = 0
    var interval:Byte = 0
}