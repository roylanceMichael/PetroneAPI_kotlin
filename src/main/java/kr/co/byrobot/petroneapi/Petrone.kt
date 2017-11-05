package kr.co.byrobot.petroneapi

import android.content.Context
import android.util.Log
import kr.co.byrobot.petroneapi.Data.PetroneAttitude
import kr.co.byrobot.petroneapi.Data.PetroneCountDrive
import kr.co.byrobot.petroneapi.Data.PetroneCountFlight
import kr.co.byrobot.petroneapi.Data.PetroneGyroBias
import kr.co.byrobot.petroneapi.Data.PetroneImageFlow
import kr.co.byrobot.petroneapi.Data.PetroneImuRawAndAngle
import kr.co.byrobot.petroneapi.Data.PetroneMotor
import kr.co.byrobot.petroneapi.Data.PetronePressure
import kr.co.byrobot.petroneapi.Data.PetroneRange
import kr.co.byrobot.petroneapi.Data.PetroneStatus
import kr.co.byrobot.petroneapi.Data.PetroneTrim
import kr.co.byrobot.petroneapi.Enum.PetroneDataType
import kr.co.byrobot.petroneapi.Enum.PetroneLightMode
import kr.co.byrobot.petroneapi.Enum.PetroneMode
import kr.co.byrobot.petroneapi.Enum.PetroneModeDrive
import kr.co.byrobot.petroneapi.Enum.PetroneModeFlight
import kr.co.byrobot.petroneapi.Packet.LedCommand.PetronePacketEmergencyStop
import kr.co.byrobot.petroneapi.Packet.LedCommand.PetronePacketLanding
import kr.co.byrobot.petroneapi.Packet.LedCommand.PetronePacketLedColor
import kr.co.byrobot.petroneapi.Packet.LedCommand.PetronePacketLedColor2
import kr.co.byrobot.petroneapi.Packet.LedCommand.PetronePacketModeChange
import kr.co.byrobot.petroneapi.Packet.LedCommand.PetronePacketRotate180
import kr.co.byrobot.petroneapi.Packet.LedCommand.PetronePacketSquare
import kr.co.byrobot.petroneapi.Packet.LedCommand.PetronePacketTakeOff
import kr.co.byrobot.petroneapi.Packet.PacketControl
import kr.co.byrobot.petroneapi.Packet.PetronePacket
import kr.co.byrobot.petroneapi.Packet.PetronePacketChangeTrim
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketAttitude
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketCountDrive
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketCountFlight
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketGyroBias
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketImageFlow
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketImuRawAndAngle
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketPressure
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketRequestTrimAll
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketRequestTrimDrive
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketRequestTrimFlight
import kr.co.byrobot.petroneapi.Packet.Request.PetronePacketStatus
import org.jetbrains.anko.doAsync

/**
 * Created by byrobot on 2017. 9. 22..
 */

class Petrone(context: Context) {
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

  var delegate: PetroneCallback? = null

  private var sendInterval: Long = 1000 / 20 // send packet max 20fps
  private var controlValue: PetroneControlValue = PetroneControlValue()

  var status: PetroneStatus? = null
  var trim: PetroneTrim? = null
  var attitude: PetroneAttitude? = null
  var gyro: PetroneGyroBias? = null
  var countOfFlight: PetroneCountFlight? = null
  var countOfDrive: PetroneCountDrive? = null
  var imuRawAndAngle: PetroneImuRawAndAngle? = null
  var pressure: PetronePressure? = null
  var imageFlow: PetroneImageFlow? = null
  var motor: PetroneMotor? = null
  var range: PetroneRange? = null

  private var isPairing: Boolean = false

  fun pairing(status: Boolean, reason: String = "") {
    isPairing = status

    if (isPairing) {
      petroneBLE?.let {
        if (it.isConnected()) {
          controller = petroneBLE
          this.sendInterval = 1000 / 20
        } else if (it.isConnected()) {
          controller = petroneWIFI
          this.sendInterval = 1000 / 40
        }

        onSend()

        if (it.isConnected()) {
          this.delegate?.connectionComplete("PETRONE BLE Conntected.")
        } else {
          petroneWIFI?.let { pwifi ->
            if (pwifi.isConnected()) {
              this.delegate?.connectionComplete("PETRONE FPV Connected.")
            }
          }
        }
      }
    } else {
      controller = null

    }
  }

  var packetList: MutableList<PetronePacket>? = mutableListOf()
  var packetFrame: Long = 0

  private fun onSend() {
    doAsync {
      while (controller != null) {
        val tempController = controller ?: return@doAsync
        if (!tempController.isConnected()) {
          return@doAsync
        }

        packetList?.let { pl ->
          if (!pl.isEmpty()) {
            val packet = pl.first()
            tempController.sendPacket(packet)
            pl.remove(packet)
          }
        }

        if (packetFrame % 10 == 0L) {
          sendPacket(PetronePacketStatus())
        }

        if (packetFrame % 2 == 0L) {
          if (controlValue.throttleTimer > 0 ||
              controlValue.yawTimer > 0 ||
              controlValue.pitchTimer > 0 ||
              controlValue.rollTimer > 0) {
            val packet = PacketControl(controlValue.throttle, controlValue.yaw,
                controlValue.pitch, controlValue.roll)

            sendPacket(packet)

            consumeControlTime(sendInterval)
          }
        }

        packetFrame += 1

        Thread.sleep(sendInterval)
      }
    }
  }

  private fun consumeControlTime(diffTime: Long) {
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

    if (controlValue.throttleTimer == 0 && controlValue.yawTimer == 0 && controlValue.pitchTimer == 0 && controlValue.rollTimer == 0) {
      val packet = PacketControl(0, 0, 0, 0)
      sendPacket(packet)
    }
  }

  fun isReadyForStart(): Boolean {
    return status?.let {
      return if (it.mode.isFlight()) {
        it.modeFlight == PetroneModeFlight.READY
      } else {
        status?.modeDrive == PetroneModeDrive.READY
      }
    } ?: false
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

  private var scanCallback: (ssid: String, name: String, rssi: Int) -> Unit = { ssid, name, rssi ->
    updateDevice(PetroneDevice(ssid, name, rssi))
  }

  private var scanErrorCallback: (error: String) -> Unit = { ssid ->
    {}
  }

  private var connectionCallback = object : PetroneConnectionCallback {
    override fun onConnected() {
      pairing(true)
      delegate?.connectionComplete("Connectd")
      Log.v("TEST", "onConnected")
    }

    override fun onDisconnected() {
      pairing(false)
      delegate?.disconnected("")
      Log.v("TEST", "onDisconnected")
    }

    override fun onRecvPacket(data: ByteArray) {
      recvPacket(data)
    }

    override fun onError(error: String) {
      pairing(false, error)
      Log.v("TEST", "onError")
    }
  }

  private fun updateDevice(device: PetroneDevice) {
    Log.v("TEST", "updateDevice : " + device.ssid + " : " + device.name)

    var isExist = false
    petroneList.forEach { existDevice ->
      if (device.ssid == existDevice.ssid) {
        isExist = true
        existDevice.rssi = device.rssi
      }
    }

    if (!isExist) {
      petroneList.add(device)
    }
  }

  fun getDeviceList(): List<PetroneDevice> {
    return petroneList
  }

  private var petroneList: MutableList<PetroneDevice> = mutableListOf()

  fun onConnect(target: String) {
    if (target.contains(Regex("FPV"))) {
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

  fun sendPacket(packet: PetronePacket) {
    packetList?.add(packet)
  }

  fun recvPacket(data: ByteArray) {
    when (PetroneDataType.newInstance(data[0])) {
      PetroneDataType.Ack -> {
        this.delegate?.recvFromPetroneResponse(data[5])
      }
      PetroneDataType.State -> {
        if (this.status == null) {
          this.status = PetroneStatus()
        }

        this.status?.parse(data)
        this.status?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.Attitude -> {
        if (this.attitude == null) {
          this.attitude = PetroneAttitude()
        }

        this.attitude?.parse(data)
        this.attitude?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.GyroBias -> {
        if (this.gyro == null) {
          this.gyro = PetroneGyroBias()
        }

        this.gyro?.parse(data)
        this.gyro?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.TrimAll -> {
        if (this.trim == null) {
          this.trim = PetroneTrim()
        }

        this.trim?.parse(data)
        this.trim?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.TrimFlight -> {
        if (this.trim == null) {
          this.trim = PetroneTrim()
        }

        this.trim?.flight?.parse(data)
        this.trim?.flight?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.TrimDrive -> {
        if (this.trim == null) {
          this.trim = PetroneTrim()
        }

        this.trim?.drive?.parse(data)
        this.trim?.drive?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.CountFlight -> {
        if (this.countOfFlight == null) {
          this.countOfFlight = PetroneCountFlight()
        }

        this.countOfFlight?.parse(data)
        this.countOfFlight?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.CountDrive -> {
        if (this.countOfDrive == null) {
          this.countOfDrive = PetroneCountDrive()
        }

        this.countOfDrive?.parse(data)
        this.countOfDrive?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.ImuRawAndAngle -> {
        if (this.imuRawAndAngle == null) {
          this.imuRawAndAngle = PetroneImuRawAndAngle()
        }

        this.imuRawAndAngle?.parse(data)
        this.imuRawAndAngle?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.Pressure -> {
        if (this.pressure == null) {
          this.pressure = PetronePressure()
        }

        this.pressure?.parse(data)
        this.pressure?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.ImageFlow -> {
        if (this.imageFlow == null) {
          this.imageFlow = PetroneImageFlow()
        }

        this.imageFlow?.parse(data)
        this.imageFlow?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.Motor -> {
        if (this.motor == null) {
          this.motor = PetroneMotor()
        }

        this.motor?.parse(data)
        this.motor?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      PetroneDataType.Range -> {
        if (this.range == null) {
          this.range = PetroneRange()
        }

        this.range?.parse(data)
        this.range?.let {
          this.delegate?.recvFromPetroneResponse(it)
        }
      }
      else -> {
        // todo: what to do
      }
    }
  }

  fun takeOff() {
    val packet = PetronePacketTakeOff()
    this.sendPacket(packet)
  }

  fun landing() {
    val packet = PetronePacketLanding()
    this.sendPacket(packet)
  }

  fun emergencyStop() {
    val packet = PetronePacketEmergencyStop()
    this.sendPacket(packet)
  }

  fun onSqure() {
    val packet = PetronePacketSquare()
    this.sendPacket(packet)
  }

  fun onRotate180() {
    val packet = PetronePacketRotate180()
    this.sendPacket(packet)
  }

  fun throttle(value: Int, millisecond: Int = 100) {
    controlValue.throttle = value
    controlValue.throttleTimer = millisecond
  }

  fun yaw(value: Int, millisecond: Int = 100) {
    controlValue.yaw = value
    controlValue.yawTimer = millisecond
  }

  fun roll(value: Int, millisecond: Int = 100) {
    controlValue.roll = value
    controlValue.rollTimer = millisecond
  }

  fun pitch(value: Int, millisecond: Int = 100) {
    controlValue.pitch = value
    controlValue.pitchTimer = millisecond
  }

  fun control(forward: Int, leftRight: Int, millisecond: Int = 100) {
    controlValue.throttle = forward
    controlValue.throttleTimer = millisecond
    controlValue.yaw = 0
    controlValue.yawTimer = 0
    controlValue.roll = leftRight
    controlValue.rollTimer = millisecond
    controlValue.pitch = 0
    controlValue.pitchTimer = 0
  }


  fun control(throttle: Int, yaw: Int, roll: Int, pitch: Int, millisecond: Int = 100) {
    controlValue.throttle = throttle
    controlValue.throttleTimer = millisecond
    controlValue.yaw = yaw
    controlValue.yawTimer = millisecond
    controlValue.roll = roll
    controlValue.rollTimer = millisecond
    controlValue.pitch = pitch
    controlValue.pitchTimer = millisecond
  }

  fun changeMode(mode: PetroneMode) {
    val packet = PetronePacketModeChange()
    packet.mode = mode
    this.sendPacket(packet)
  }

  fun changeTrim(throttle: Short, yaw: Short, roll: Short, pitch: Short, wheel: Short) {
    val packet = PetronePacketChangeTrim()
    packet.flight.throttle = throttle
    packet.flight.yaw = yaw
    packet.flight.roll = roll
    packet.flight.pitch = pitch
    packet.drive.wheel = wheel

    this.sendPacket(packet)
  }

  fun changeTrim(throttle: Short, yaw: Short, roll: Short, pitch: Short) {
    val packet = PetronePacketChangeTrim()
    packet.flight.throttle = throttle
    packet.flight.yaw = yaw
    packet.flight.roll = roll
    packet.flight.pitch = pitch
    this.trim?.let {
      packet.drive.wheel = it.drive.wheel
    }

    this.sendPacket(packet)
  }

  fun changeTrim(wheel: Short) {
    val packet = PetronePacketChangeTrim()
    packet.drive.wheel = wheel

    this.trim?.let {
      packet.flight.throttle = it.flight.throttle
      packet.flight.yaw = it.flight.yaw
      packet.flight.roll = it.flight.roll
      packet.flight.pitch = it.flight.pitch
    }
    this.sendPacket(packet)
  }

  fun color(red: Byte, green: Byte, blue: Byte) {
    val packet = PetronePacketLedColor2()
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

  fun color(eyeRed: Byte, eyeGreen: Byte, eyeBlue: Byte, armRed: Byte, armGreen: Byte,
      armBlue: Byte) {
    val packet = PetronePacketLedColor2()
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

  fun colorForEye(red: Byte, green: Byte, blue: Byte) {
    val packet = PetronePacketLedColor()
    packet.led.mode = PetroneLightMode.EyeHold.mode
    packet.led.red = red
    packet.led.green = green
    packet.led.blue = blue
    packet.led.interval = 255.toByte()

    this.sendPacket(packet)
  }

  fun colorForArm(red: Byte, green: Byte, blue: Byte) {
    val packet = PetronePacketLedColor()
    packet.led.mode = PetroneLightMode.ArmHold.mode
    packet.led.red = red
    packet.led.green = green
    packet.led.blue = blue
    packet.led.interval = 255.toByte()

    this.sendPacket(packet)
  }

  fun requestState() {
    val packet = PetronePacketStatus()
    this.sendPacket(packet)
  }

  fun requestAttitude() {
    val packet = PetronePacketAttitude()
    this.sendPacket(packet)
  }

  fun requestGyroBias() {
    val packet = PetronePacketGyroBias()
    this.sendPacket(packet)
  }

  fun requestTrimAll() {
    val packet = PetronePacketRequestTrimAll()
    this.sendPacket(packet)
  }

  fun requestTrimFlight() {
    val packet = PetronePacketRequestTrimFlight()
    this.sendPacket(packet)
  }

  fun requestTrimDrive() {
    val packet = PetronePacketRequestTrimDrive()
    this.sendPacket(packet)
  }

  fun requestCountFlight() {
    val packet = PetronePacketCountFlight()
    this.sendPacket(packet)
  }

  fun requestCountDrive() {
    val packet = PetronePacketCountDrive()
    this.sendPacket(packet)
  }

  fun requestImuRawAndAngle() {
    val packet = PetronePacketImuRawAndAngle()
    this.sendPacket(packet)
  }

  fun requestPressure() {
    val packet = PetronePacketPressure()
    this.sendPacket(packet)
  }

  fun requestImageFlow() {
    val packet = PetronePacketImageFlow()
    this.sendPacket(packet)
  }

  init {
    petroneBLE = PetroneBLE(context)
    petroneWIFI = PetroneWIFI(context)
  }
}