package kr.co.byrobot.openapi

import android.content.Context
import android.util.Log
import kr.co.byrobot.openapi.Data.*
import kr.co.byrobot.openapi.Enum.*
import kr.co.byrobot.openapi.Packet.LedCommand.*
import kr.co.byrobot.openapi.Packet.Request.*
import kr.co.byrobot.openapi.Packet.*
import org.jetbrains.anko.doAsync

/**
 * Created by byrobot on 2017. 9. 22..
 */

class Petrone {
    private class PetroneControlValue {
        var throttle: Int = 0
        var throttleTimer: Int = 0
        var yaw: Int = 0
        var yawTimer: Int = 0
        var roll: Int = 0
        var rollTimer: Int = 0
        var pitch: Int = 0
        var pitchTimer: Int = 0
    }

    private var controller: PetroneNetworkInterface? = null
    private var petroneBLE: PetroneBLE? = null
    private var petroneWIFI: PetroneWIFI? = null

    var delegate : PetroneCallback? = null

    private var sendInterval: Long = 1000 / 20 // send packet max 20fps
    private var controlValue : PetroneControlValue = PetroneControlValue()

    var status:PetroneStatus?                = null
    var trim:PetroneTrim?                    = null
    var attitude:PetroneAttitude?            = null
    var gyro:PetroneGyroBias?                = null
    var countOfFlight:PetroneCountFlight?    = null
    var countOfDrive:PetroneCountDrive?      = null
    var imuRawAndAngle:PetroneImuRawAndAngle? = null
    var pressure:PetronePressure?            = null
    var imageFlow:PetroneImageFlow?          = null
    var motor:PetroneMotor?                  = null
    var range:PetroneRange?                  = null

    init {
    }

    constructor(context:Context) {
        petroneBLE = PetroneBLE(context)
        petroneWIFI = PetroneWIFI(context)
    }

    private var isPairing : Boolean = false

    fun pairing(status:Boolean, reason:String = "" ) {
        isPairing = status

        if ( isPairing ) {
            if (petroneBLE!!.isConnected()) {
                controller = petroneBLE
                this.sendInterval = 1000 / 20
            } else if (petroneWIFI!!.isConnected()) {
                controller = petroneWIFI
                this.sendInterval = 1000 / 40
            }

            onSend()

            if (petroneBLE!!.isConnected()) {
                this.delegate?.connectionComplete("PETRONE BLE Conntected.")
            } else  if (petroneWIFI!!.isConnected()) {
                this.delegate?.connectionComplete("PETRONE FPV Conntected.")
            }
        } else {
            controller = null

        }
    }

    var packetList: MutableList<PetronePacket>?= mutableListOf()
    var packetFrame: Long = 0

    private fun onSend() {
        doAsync {
            while( controller != null && controller!!.isConnected() ) {
                if (packetList!!.isEmpty() != true) {

                    val packet: PetronePacket = packetList!!.first()

                    if (packet != null) {
                        controller!!.sendPacket(packet)
                        packetList!!.remove(packet)
                    }
                }

                if( packetFrame % 10 == 0L ) {
                    sendPacket(PetronePacketStatus())
                }

                if( packetFrame % 2 == 0L ) {
                    if ( controlValue.throttleTimer > 0 || controlValue.yawTimer > 0 || controlValue.pitchTimer > 0 || controlValue.rollTimer > 0 ) {
                        val packet:PacketControl =  PacketControl(controlValue.throttle, controlValue.yaw, controlValue.pitch, controlValue.roll)

                        sendPacket(packet)

                        consumeControlTime(sendInterval)
                    }
                }

                packetFrame += 1

                Thread.sleep( sendInterval )
            }
        }
    }

    private fun consumeControlTime( diffTime:Long ) {
        this.controlValue.throttleTimer -= diffTime.toInt()
        this.controlValue.yawTimer -= diffTime.toInt()
        this.controlValue.pitchTimer -= diffTime.toInt()
        this.controlValue.rollTimer -= diffTime.toInt()

        if (this.controlValue.throttleTimer <= 0) {
            this.controlValue.throttle = 0
            this.controlValue.throttleTimer = 0
        }
        if (this.controlValue.yawTimer <= 0) {
            this.controlValue.yaw = 0
            this.controlValue.yawTimer = 0
        }
        if (this.controlValue.pitchTimer <= 0) {
            this.controlValue.roll = 0
            this.controlValue.pitchTimer = 0
        }
        if (this.controlValue.rollTimer <= 0) {
            this.controlValue.roll = 0
            this.controlValue.rollTimer = 0
        }

        if ( controlValue.throttleTimer == 0 && controlValue.yawTimer == 0 && controlValue.pitchTimer == 0 && controlValue.rollTimer == 0 ) {
            val packet:PacketControl =  PacketControl(0,0,0,0)

            sendPacket(packet)
        }
    }

    fun isReadyForStart() : Boolean {
        if (status!!.mode.isFlight())  {
            if( status?.modeFlight == PetroneModeFlight.READY ) {
                return true
            } else {
                return false
            }
        } else {
            if( status?.modeDrive == PetroneModeDrive.READY ) {
                return true
            } else {
                return false
            }
        }
    }

    fun onScan() {
        petroneList.clear()

        petroneBLE?.scan(scanCallback, scanErrorCallback)
        petroneWIFI?.scan(scanCallback, scanErrorCallback)
    }

    fun onStopScan() {
        if (petroneBLE != null) {
            petroneBLE?.stopScan()
        }

        if (petroneBLE != null) {
            petroneWIFI?.stopScan()
        }
    }

    private var scanCallback : (ssid:String, name:String, rssi:Int) -> Unit = {
        ssid, name, rssi ->  updateDevice(PetroneDevice(ssid, name, rssi))
    }

    private var scanErrorCallback : (error:String) -> Unit = {
        ssid -> {}
    }

    private var connectionCallback = object : PetroneConnectionCallback {
        override fun onConnected() {
            pairing(true)
            delegate!!.connectionComplete("Connectd")
            Log.v("TEST", "onConnected")
        }
        override fun onDisconnected(){
            pairing(false)
            delegate!!.disconnected("")
            Log.v("TEST", "onDisconnected")
        }
        override fun onRecvPacket(data:ByteArray) {
            recvPacket(data)
        }
        override fun onError(error: String){
            pairing(false,error)
            Log.v("TEST", "onError")
        }
    }

    private fun updateDevice(device:PetroneDevice)  {
        Log.v("TEST", "updateDevice : " + device.ssid + " : " + device.name)

        var isExist = false
        petroneList.forEach {
            existDevice ->
            if( device.ssid.equals(existDevice.ssid) ) {
                isExist = true
                existDevice.rssi = device.rssi
            }
        }

        if( !isExist ) {
            petroneList.add(device)
        }
    }

    fun getDeviceList() : List<PetroneDevice> {
        return petroneList
    }

    private var petroneList:MutableList<PetroneDevice> = mutableListOf()

    fun onConnect(target:String) {
        if (target.contains(Regex("FPV")))  {
            petroneWIFI?.connect(target, connectionCallback)
            petroneBLE?.stopScan()
        } else {
            petroneBLE?.connect(target, connectionCallback)
            petroneWIFI?.stopScan()
        }

        petroneList.clear()
    }

    fun onDisconnect() {
        petroneBLE?.disconnect()
        petroneWIFI?.disconnect()
        petroneList.clear()
    }

    fun sendPacket(packet:PetronePacket) {
        packetList!!.add(packet)
    }

    fun recvPacket(data:ByteArray) {
        when (  PetroneDataType.newInstance(data[0])  ) {
            PetroneDataType.Ack -> {
                this.delegate?.recvFromPetroneResponse(data[5])
            }
            PetroneDataType.State -> {
                if (this.status == null) {
                    this.status = PetroneStatus()
                }

                this.status?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.status!!)
            }
            PetroneDataType.Attitude -> {
                if (this.attitude == null) {
                    this.attitude = PetroneAttitude()
                }

                this.attitude?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.attitude!!)
            }
            PetroneDataType.GyroBias -> {
                if (this.gyro == null) {
                    this.gyro = PetroneGyroBias()
                }

                this.gyro?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.gyro!!)
            }
            PetroneDataType.TrimAll -> {
                if (this.trim == null) {
                    this.trim = PetroneTrim()
                }

                this.trim?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.trim!!)
            }
            PetroneDataType.TrimFlight -> {
                if (this.trim == null) {
                    this.trim = PetroneTrim()
                }

                this.trim?.flight?.parse(data);
                this.delegate?.recvFromPetroneResponse((this.trim?.flight)!!)
            }
            PetroneDataType.TrimDrive -> {
                if (this.trim == null) {
                    this.trim = PetroneTrim()
                }

                this.trim?.drive?.parse(data)
                this.delegate?.recvFromPetroneResponse((this.trim?.drive)!!)
            }
            PetroneDataType.CountFlight -> {
                if (this.countOfFlight == null) {
                    this.countOfFlight = PetroneCountFlight()
                }

                this.countOfFlight?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.countOfFlight!!)
            }
            PetroneDataType.CountDrive -> {
                if (this.countOfDrive == null) {
                    this.countOfDrive = PetroneCountDrive()
                }

                this.countOfDrive?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.countOfDrive!!)
            }
            PetroneDataType.ImuRawAndAngle -> {
                if (this.imuRawAndAngle == null) {
                    this.imuRawAndAngle = PetroneImuRawAndAngle()
                }

                this.imuRawAndAngle?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.imuRawAndAngle!!)
            }
            PetroneDataType.Pressure -> {
                if (this.pressure == null) {
                    this.pressure = PetronePressure()
                }

                this.pressure?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.pressure!!)
            }
            PetroneDataType.ImageFlow -> {
                if (this.imageFlow == null) {
                    this.imageFlow = PetroneImageFlow()
                }

                this.imageFlow?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.imageFlow!!)
            }
            PetroneDataType.Motor -> {
                if (this.motor == null) {
                    this.motor = PetroneMotor()
                }

                this
                        .motor?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.motor!!)
            }
            PetroneDataType.Range -> {
                if (this.range == null) {
                    this.range = PetroneRange()
                }

                this.range?.parse(data);
                this.delegate?.recvFromPetroneResponse(this.range!!)
            }
        }
    }

    fun takeOff() {
        val packet:PetronePacketTakeOff = PetronePacketTakeOff()
        this.sendPacket(packet)
    }

    fun landing() {
        val packet:PetronePacketLanding = PetronePacketLanding()
        this.sendPacket(packet)
    }

    fun emergencyStop() {
        val packet:PetronePacketEmergencyStop = PetronePacketEmergencyStop()
        this.sendPacket(packet)
    }

    fun onSqure() {
        val packet:PetronePacketSquare = PetronePacketSquare()
        this.sendPacket(packet)
    }

    fun onRotate180() {
        val packet:PetronePacketRotate180 = PetronePacketRotate180()
        this.sendPacket(packet)
    }

    fun throttle(value:Int, millisecond:Int = 100) {
        controlValue.throttle = value
        controlValue.throttleTimer = millisecond
    }

    fun yaw(value:Int, millisecond:Int = 100) {
        controlValue.yaw = value
        controlValue.yawTimer = millisecond
    }

    fun roll(value:Int, millisecond:Int = 100) {
        controlValue.roll = value
        controlValue.rollTimer = millisecond
    }

    fun pitch(value:Int, millisecond:Int = 100) {
        controlValue.pitch = value
        controlValue.pitchTimer = millisecond
    }

    fun control(forward:Int, leftRight:Int, millisecond:Int = 100) {
        controlValue.throttle = forward
        controlValue.throttleTimer = millisecond
        controlValue.yaw = 0
        controlValue.yawTimer = 0
        controlValue.roll = leftRight
        controlValue.rollTimer = millisecond
        controlValue.pitch = 0
        controlValue.pitchTimer = 0
    }


    fun control(throttle:Int, yaw:Int, roll:Int, pitch:Int, millisecond:Int = 100) {
        controlValue.throttle = throttle
        controlValue.throttleTimer = millisecond
        controlValue.yaw = yaw
        controlValue.yawTimer = millisecond
        controlValue.roll = roll
        controlValue.rollTimer = millisecond
        controlValue.pitch = pitch
        controlValue.pitchTimer = millisecond
    }

    fun changeMode(mode:PetroneMode) {
        var packet:PetronePacketModeChange = PetronePacketModeChange()
        packet.mode = mode
        this.sendPacket(packet)
    }

    fun changeTrim(throttle:Short, yaw:Short, roll:Short, pitch:Short, wheel:Short) {
        var packet:PetronePacketChangeTrim = PetronePacketChangeTrim()
        packet.flight.throttle = throttle
        packet.flight.yaw = yaw
        packet.flight.roll = roll
        packet.flight.pitch = pitch
        packet.drive.wheel = wheel

        this.sendPacket(packet)
    }

    fun changeTrim(throttle:Short, yaw:Short, roll:Short, pitch:Short) {
        var packet:PetronePacketChangeTrim = PetronePacketChangeTrim()
        packet.flight.throttle = throttle
        packet.flight.yaw = yaw
        packet.flight.roll = roll
        packet.flight.pitch = pitch
        if( this.trim != null ) {
            packet.drive.wheel = this.trim!!.drive.wheel
        }

        this.sendPacket(packet)
    }

    fun changeTrim(wheel:Short) {
        var packet:PetronePacketChangeTrim = PetronePacketChangeTrim()
        packet.drive.wheel = wheel

        if( this.trim != null ) {
            packet.flight.throttle = this.trim!!.flight.throttle
            packet.flight.yaw = this.trim!!.flight.yaw
            packet.flight.roll = this.trim!!.flight.roll
            packet.flight.pitch = this.trim!!.flight.pitch
        }
        this.sendPacket(packet)
    }

    fun color(red:Byte, green:Byte, blue:Byte) {
        var packet:PetronePacketLedColor2 = PetronePacketLedColor2()
        packet.led1.mode = PetroneLightMode.EyeHold.mode
        packet.led1.red = red
        packet.led1.green = green
        packet.led1.blue = blue
        packet.led1.interval = 255.toByte()
        packet.led2.mode = PetroneLightMode.ArmHold.mode
        packet.led2.red = red
        packet.led2.green = green
        packet.led2.blue = blue
        packet.led2.interval = 255.toByte()

        this.sendPacket(packet)
    }

    fun color(eyeRed:Byte, eyeGreen:Byte, eyeBlue:Byte, armRed:Byte, armGreen:Byte, armBlue:Byte) {
        var packet:PetronePacketLedColor2 = PetronePacketLedColor2()
        packet.led1.mode = PetroneLightMode.EyeHold.mode
        packet.led1.red = eyeRed
        packet.led1.green = eyeGreen
        packet.led1.blue = eyeBlue
        packet.led1.interval = 255.toByte()
        packet.led2.mode = PetroneLightMode.ArmHold.mode
        packet.led2.red = armRed
        packet.led2.green = armGreen
        packet.led2.blue = armBlue
        packet.led2.interval = 255.toByte()

        this.sendPacket(packet)
    }

    fun colorForEye(red:Byte, green:Byte, blue:Byte) {
        var packet:PetronePacketLedColor = PetronePacketLedColor()
        packet.led.mode = PetroneLightMode.EyeHold.mode
        packet.led.red = red
        packet.led.green = green
        packet.led.blue = blue
        packet.led.interval = 255.toByte()

        this.sendPacket(packet)
    }

    fun colorForArm(red:Byte, green:Byte, blue:Byte) {
        var packet:PetronePacketLedColor = PetronePacketLedColor()
        packet.led.mode = PetroneLightMode.ArmHold.mode
        packet.led.red = red
        packet.led.green = green
        packet.led.blue = blue
        packet.led.interval = 255.toByte()

        this.sendPacket(packet)
    }
    
    fun requestState() {
        val packet:PetronePacketStatus = PetronePacketStatus()
        this.sendPacket(packet)
    }

    fun requestAttitude() {
        val packet:PetronePacketAttitude = PetronePacketAttitude()
        this.sendPacket(packet)
    }

    fun requestGyroBias() {
        val packet:PetronePacketGyroBias = PetronePacketGyroBias()
        this.sendPacket(packet)
    }

    fun requestTrimAll() {
        val packet:PetronePacketRequestTrimAll = PetronePacketRequestTrimAll()
        this.sendPacket(packet)
    }

    fun requestTrimFlight() {
        val packet:PetronePacketRequestTrimFlight = PetronePacketRequestTrimFlight()
        this.sendPacket(packet)
    }

    fun requestTrimDrive() {
        val packet:PetronePacketRequestTrimDrive = PetronePacketRequestTrimDrive()
        this.sendPacket(packet)
    }

    fun requestCountFlight() {
        val packet:PetronePacketCountFlight = PetronePacketCountFlight()
        this.sendPacket(packet)
    }

    fun requestCountDrive() {
        val packet:PetronePacketCountDrive = PetronePacketCountDrive()
        this.sendPacket(packet)
    }

    fun requestImuRawAndAngle() {
        val packet:PetronePacketImuRawAndAngle = PetronePacketImuRawAndAngle()
        this.sendPacket(packet)
    }

    fun requestPressure() {
        val packet:PetronePacketPressure = PetronePacketPressure()
        this.sendPacket(packet)
    }

    fun requestImageFlow() {
        val packet:PetronePacketImageFlow = PetronePacketImageFlow()
        this.sendPacket(packet)
    }
}