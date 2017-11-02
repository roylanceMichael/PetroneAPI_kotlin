package kr.co.byrobot.petroneapi.Packet

import kr.co.byrobot.petroneapi.Data.PetroneTrimDrive
import kr.co.byrobot.petroneapi.Data.PetroneTrimFlight
import kr.co.byrobot.petroneapi.Enum.PetroneDataType

/**
 * Created by byrobot on 2017. 9. 27..
 */
class PetronePacketTrimAll : PetronePacket {
    override var size: Int = 10

    var flight: PetroneTrimFlight = PetroneTrimFlight()
    var drive: PetroneTrimDrive = PetroneTrimDrive()

    override fun decode(data: PetroneByteArray) : Boolean{
        if( data.size == 11 ) {
            flight.roll     = data.get16 (1)
            flight.pitch    = data.get16 (3)
            flight.yaw      = data.get16 (5)
            flight.throttle = data.get16 (7)
            drive.wheel     = data.get16 (9)
            return true
        }
        return false
    }

    override fun encode() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 1);
        data.set(value = PetroneDataType.TrimAll)
        data.set(value = flight.roll)
        data.set(value = flight.pitch)
        data.set(value = flight.yaw)
        data.set(value = flight.throttle)
        data.set(value = drive.wheel)
        return data
    }

    override fun encodeSerial() : PetroneByteArray {
        var data : PetroneByteArray = PetroneByteArray(size + 2);
        data.set(value = PetroneDataType.Request);
        data.set(value = size.toByte());
        data.set(value = flight.roll)
        data.set(value = flight.pitch)
        data.set(value = flight.yaw)
        data.set(value = flight.throttle)
        data.set(value = drive.wheel)
        return data
    }
}