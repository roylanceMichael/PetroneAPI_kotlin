package kr.co.byrobot.petroneapi.BLE

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue


/**
 * Created by byrobot on 2017. 7. 18..
 */
open class BLEService : Service() {
  private var mHandler: BLEServiceHandle? = null
  private var mMessanger: Messenger? = null
  private var mGatt: BluetoothGatt? = null
  private var mScanner: BLEScanner? = null

  //Messenger queue
  private var mScanMessenger: Messenger? = null
  private val mClients = LinkedList<Messenger>()
  protected val writeQueue: Queue<Any> = LinkedBlockingQueue()
  protected var isWriting: Boolean = false

  var mState: BLEConnectState = BLEConnectState.INITIALED

  init {
    mHandler = BLEServiceHandle(this)
    mMessanger = Messenger(mHandler)
  }

  override fun onBind(intent: Intent): IBinder {
    return mMessanger?.binder!!
  }

  var callback: PetroneBLEScanCallback =
      object : PetroneBLEScanCallback {
        override fun onBleScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
          if (mState != BLEConnectState.SCANNING) return
          if (device.name != null
              && device.name.contains(Regex("^PETRONE +[a-z0-9]{4}"))
              && mScanMessenger != null) {

            var show = Math.max(-100, Math.min(0, rssi + 45))

            val msg = Message.obtain(null, BLEConstants.MSG_CONTROL_ID_FIND_DEVICE.code)
            if (msg != null) {
              msg.obj = device
              msg.arg1 = rssi
              mScanMessenger?.send(msg)
            }
          }
        }

        override fun onBleScanFailed(scanState: BLEScanState) {
          val msg = Message.obtain(null, BLEConstants.MSG_CONTROL_ID_SCAN_STOPED.code)
          if (msg != null) {
            mScanMessenger!!.send(msg)
          }
        }
      }

  fun startScan() {
    if (mScanner == null) {
      mScanner = BLEScanner(this, callback)
    }

    mState = BLEConnectState.SCANNING
    mScanner?.startBleScan(10000)
  }

  fun stopScan() {
    if (mScanner != null) {
      mScanner?.stopBleScan()
      mScanner = null
      mState = BLEConnectState.INITIALED
    }
  }

  fun connectDevice(device: BluetoothDevice): Boolean {
    return connectDevice(device, false)
  }

  fun connectDevice(device: BluetoothDevice, autoConnect: Boolean): Boolean {
    mGatt = device.connectGatt(this, autoConnect, mGattCallback)

    if (mGatt != null) {
      mHandler?.post(
          Runnable {
            kotlin.run {
              mGatt?.connect()
            }
          })
    }

    return true
  }

  fun connectDevice(device: String): Boolean {
    return connectDevice(device, false)
  }

  fun connectDevice(device: String, autoConnect: Boolean): Boolean {
    val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    return connectDevice(bluetoothManager.adapter.getRemoteDevice(device), autoConnect)
  }


  fun updateState(newState: BLEConnectState) {
    if (mState !== newState) {
      mState = newState
      val msg = Message.obtain(null, BLEConstants.BLE_MSG_ID_CONNECTION_STATE_CHANGED.code)
      if (msg != null) {
        msg.obj = mState
        broadcastClients(msg)
      }
    }
  }

  fun broadcastClients(msg: Message) {
    mClients.forEach { messenger ->
      if (!sendMessage(messenger, msg)) mClients.remove()
    }
  }

  fun sendMessage(messenger: Messenger, msg: Message): Boolean {
    try {
      messenger.send(msg)
    } catch (ex: RemoteException) {
      return false
    }

    return true
  }

  private fun sendBleMessage(msgId: Int, status: Int, values: ByteArray, uuid: UUID?) {
    val msg = Message.obtain(null, msgId, status, 0, values)
    val data = Bundle()

    // set PETRONE characteristic UUID.
    data.putSerializable("C320DF02-7891-11E5-8BCF-FEFF819CDC9F", uuid)
    msg.data = data

    broadcastClients(msg)
  }

  private fun sendBleMessage(msgId: Int, status: Int, uuid: UUID?) {
    val msg = Message.obtain(null, msgId, status)
    val data = Bundle()

    // set PETRONE characteristic UUID.
    data.putSerializable("C320DF02-7891-11E5-8BCF-FEFF819CDC9F", uuid)
    msg.data = data

    broadcastClients(msg)
  }

  protected fun write(o: Any) {
    if (writeQueue.isEmpty() && !isWriting) {
      doWrite(o)
    } else {
      writeQueue.add(o)
    }
  }

  private fun onNextWrite() {
    isWriting = false
    nextWrite()
  }

  private fun nextWrite() {
    //empty enable write
    if (isWriting) {
      isWriting = !writeQueue.isEmpty()
    }
    if (!writeQueue.isEmpty() && !isWriting) {
      doWrite(writeQueue.poll())
    }
  }

  private fun doWrite(o: Any) {
    if (o is BluetoothGattCharacteristic) {
      isWriting = mGatt!!.writeCharacteristic(o)
    } else if (o is BluetoothGattDescriptor) {
      isWriting = mGatt!!.writeDescriptor(o)
    } else {
      nextWrite()
    }
  }

  private var mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
      when (newState) {
        BluetoothProfile.STATE_CONNECTED -> {
          updateState(BLEConnectState.CONNECTED)
          gatt?.discoverServices()
        }
        BluetoothProfile.STATE_CONNECTING -> {
          updateState(BLEConnectState.CONNECTING)

        }
        BluetoothProfile.STATE_DISCONNECTING -> {
          updateState(BLEConnectState.DISCONNECTING)

        }
        BluetoothProfile.STATE_DISCONNECTED -> {
          updateState(BLEConnectState.DISCONNECTED)
          isWriting = false
          writeQueue.clear()
        }
        else -> super.onConnectionStateChange(gatt, status, newState)
      }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
      when (status) {
        BluetoothGatt.GATT_SUCCESS -> {
          onDiscoverServices(gatt)
          updateState(BLEConnectState.SERVICE_IS_DISCOVERED)
        }
        else -> {
          if (mState != BLEConnectState.SERVICE_IS_NOT_DISCOVERED) {
            updateState(mState)
          }
        }
      }
      super.onServicesDiscovered(gatt, status)
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?,
        status: Int) {
      sendBleMessage(BLEConstants.MSG_BLE_ID_DESCRIPTOR_WRITE.code, status, descriptor?.uuid)
      onNextWrite()
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?) {
      characteristic?.let {
        sendBleMessage(BLEConstants.MSG_BLE_ID_CHARACTERISTIC_NOTIFICATION.code,
            BluetoothGatt.GATT_SUCCESS, it.value, characteristic.uuid)
        onNextWrite()
      }
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?, status: Int) {
      super.onCharacteristicWrite(gatt, characteristic, status)
      onNextWrite()
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?, status: Int) {
      characteristic?.let {
        sendBleMessage(BLEConstants.MSG_BLE_ID_CHARACTERISTIC_READ.code, status,
            it.value, characteristic.uuid)
        onNextWrite()
      }
    }

    override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?,
        status: Int) {
      descriptor?.let {
        sendBleMessage(BLEConstants.MSG_BLE_ID_DESCRIPTOR_READ.code, status, descriptor.value,
            descriptor.uuid)
        onNextWrite()
      }
    }

    override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
      val msg = Message.obtain(null, BLEConstants.MSG_BLE_ID_RELIABLE_WRITE_COMPLETED.code, status)
      broadcastClients(msg)
      onNextWrite()
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
      val msg = Message.obtain(null, BLEConstants.MSG_BLE_ID_READ_REMOTE_RSSI.code, status, rssi)
      broadcastClients(msg)
      onNextWrite()
    }

    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
      val msg = Message.obtain(null, BLEConstants.MSG_BLE_ID_MTU_CHANGED.code, status, mtu)
      broadcastClients(msg)
      onNextWrite()
    }
  }


  fun onDiscoverServices(gatt: BluetoothGatt?) {
    gatt?.let {
      val gattService = it.getService(
          UUID.fromString("C320DF00-7891-11E5-8BCF-FEFF819CDC9F")) as BluetoothGattService
      val recvConf = gattService.getCharacteristic(
          UUID.fromString("C320DF01-7891-11E5-8BCF-FEFF819CDC9F"))

      if (recvConf.descriptors != null && !recvConf.descriptors.isEmpty()) {
        recvConf.descriptors.forEach { descriptor ->
          if (recvConf.properties.or(BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            updateCharacteristicNotification(
                UUID.fromString("C320DF00-7891-11E5-8BCF-FEFF819CDC9F"),
                UUID.fromString("C320DF01-7891-11E5-8BCF-FEFF819CDC9F"),
                descriptor.uuid, true)
          }
        }

      }
    }

  }

  /**
   * enable notify or disable notify
   */
  fun updateCharacteristicNotification(serviceUUID: UUID, CharacteristicUUID: UUID,
      descriptorUUID: UUID, enable: Boolean) {
    if (mGatt == null) return

    val service = mGatt?.getService(serviceUUID)

    if (service != null) {
      val readData = service.getCharacteristic(CharacteristicUUID)
      mGatt?.setCharacteristicNotification(readData, enable)

      val config = readData.getDescriptor(descriptorUUID)
      if (config != null) {
        if (enable)
          config.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        else
          config.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE

        write(config)
      }
    }
  }

  fun writeToCharacteristic(serviceUUID: UUID, characteristicUUID: UUID,
      values: ByteArray): Boolean {
    val gattService = if (mGatt == null) null else mGatt?.getService(serviceUUID)
    if (gattService != null) {
      val gattCharacteristic = gattService.getCharacteristic(characteristicUUID)
      if (gattCharacteristic != null) {
        gattCharacteristic.value = values
        write(gattCharacteristic)
        return true
      }
    }
    return false
  }

  fun readFromCharacteristic(serviceUUID: UUID, CharacteristicUUID: UUID): Boolean {
    val gattService = mGatt?.getService(serviceUUID)
    if (gattService != null) {
      val gattCharacteristic = gattService.getCharacteristic(CharacteristicUUID)
      if (gattCharacteristic != null) {

        mGatt?.setCharacteristicNotification(gattCharacteristic, true)
        mGatt?.readCharacteristic(gattCharacteristic)
        return true
      }
    }

    return false
  }

  fun setScanCallback(messenger: Messenger) {
    mScanMessenger = messenger
  }


  fun addClient(messenger: Messenger) {
    mClients.add(messenger)
  }

  fun removeClient(messenger: Messenger) {
    mClients.remove(messenger)
    if (mClients.isEmpty()) {
      release()
    }
  }

  fun release() {
    isWriting = false
    writeQueue.clear()

    mHandler?.post(Runnable { kotlin.run { if (mGatt != null) mGatt?.disconnect() } })
    mHandler?.postDelayed(Runnable { kotlin.run { if (mGatt != null) mGatt?.disconnect() } }, 100)
  }
}