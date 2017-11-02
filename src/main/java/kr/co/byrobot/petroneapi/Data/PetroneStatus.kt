package kr.co.byrobot.petroneapi.Data

import kr.co.byrobot.petroneapi.Enum.*

/**
 * Created by byrobot on 2017. 9. 22..
 */
class PetroneStatus {
    var mode: PetroneMode = PetroneMode.NONE
    var modeSystem: PetroneModeSystem = PetroneModeSystem.NONE
    var modeFlight: PetroneModeFlight = PetroneModeFlight.NONE
    var modeDrive: PetroneModeDrive = PetroneModeDrive.NONE
    var sensorOrientation: PetroneSensorOrientation = PetroneSensorOrientation.NONE
    var coordinate: PetroneCoordinate = PetroneCoordinate.NONE
    var battery: Byte = 0

    fun parse(data:ByteArray) {
        this.mode = PetroneMode.fromByte(data[1])
        this.modeSystem = PetroneModeSystem.fromByte(data[2])
        this.modeFlight = PetroneModeFlight.fromByte(data[3])
        this.modeDrive = PetroneModeDrive.fromByte(data[4])
        this.sensorOrientation = PetroneSensorOrientation.fromByte(data[5])
        this.coordinate = PetroneCoordinate.fromByte(data[6])
        this.battery = data[7]
    }
}