package kr.co.byrobot.petroneapi.Data

import kr.co.byrobot.petroneapi.Enum.PetroneLightMode

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetroneLedBase {
    var mode:Byte = PetroneLightMode.NONE.mode
    var red:Byte = 0
    var green:Byte = 0
    var blue:Byte = 0
    var interval:Byte = 0
}