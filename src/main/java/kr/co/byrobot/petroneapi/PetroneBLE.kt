package kr.co.byrobot.petroneapi

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.util.Log
import kr.co.byrobot.petroneapi.BLE.BLEConnectState
import kr.co.byrobot.petroneapi.BLE.BLEHelper
import kr.co.byrobot.petroneapi.BLE.PetroneBLECallback
import kr.co.byrobot.petroneapi.Packet.PetronePacket
import java.util.UUID


/**
 * Created by byrobot on 2017. 7. 10..
 */


class PetroneBLE(context: Context) : PetroneNetworkInterface, BLEHelper.OnBindListener {
  private val TAG = PetroneBLE::class.java.name

  private val REQUEST_NUM_BLE_ON = 1
  private val SEND_VALUE_LENGTH = 20

  private var mDeviceAddress: String = ""
  private var mBLEHelper: BLEHelper? = null
  private var context: Context? = null

  private var connected: Boolean = false

  override fun initWithCallback(callback: PetroneCallback) {

  }

  override fun scan(onScan: (ssid: String, name: String, rssi: Int) -> Unit,
      onScanFailure: (error: String) -> Unit) {
    val isBinded = mBLEHelper?.isBinded()
    if (isBinded == null || !isBinded) {
      val bindResult = mBLEHelper?.bindService(this)
      if (bindResult != null && bindResult) {
        Log.v(TAG, "Service bind failed")
      }
    }

    mBLEHelper?.startScanDevice(onScan, onScanFailure)
  }

  override fun stopScan() {
    mBLEHelper?.stopScanDevice()
  }

  private var connectionCallback: PetroneConnectionCallback? = null
  override fun connect(target: String, callback: PetroneConnectionCallback) {
    connectionCallback = callback
    mBLEHelper?.connectDevice(target, callback);
  }

  override fun isConnected(): Boolean {
    return connected
  }

  override fun getConnectionList(): List<String> {

    return mutableListOf()
  }

  override fun disconnect(target: String) {
    connected = false
    mDeviceAddress = ""
    context?.let {
      mBLEHelper?.release()
      mBLEHelper = BLEHelper(it, mBleCallback)
      mBLEHelper?.bindService(this)
    }

  }

  override fun sendPacket(packet: PetronePacket) {
    mBLEHelper?.writeCharacteristic(packet.encode().toByteArray())
  }

  override fun recvPacket(data: ByteArray) {
    connectionCallback?.let {
      it.onRecvPacket(data)
    }
  }

  override fun onUpdateBLEDevice(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
  }

  override fun onServiceConnected() {
  }

  private val mBleCallback = object : PetroneBLECallback {
    override fun onFailed(msg: String) {}

    override fun onDescriptorWrite(uuid: UUID, status: Int) {}

    override fun onCharacteristicRead(uuid: UUID, data: ByteArray) {
      //String values = HexUtil.encodeHexStr(data);

      //Log.v(TAG, "onCharacteristicRead: " + values);
    }

    override fun onCharacteristicNotification(uuid: UUID, data: ByteArray) {
      recvPacket(data)
    }

    override fun onCharacteristicWrite(uuid: UUID, status: Int) {
      if (status != BluetoothGatt.GATT_SUCCESS) {
        Log.v(TAG, "onCharacteristicWrite: " + status.toString());
      }
    }

    override fun onConnectionStateChange(status: Int, newStatus: Int) {
      val connectState = BLEConnectState.newInstance(newStatus)
      if (connectState.needConnect()) {
        disconnect("")
      } else if (connectState.isConnected()) {
        if (!connected) {
          connected = true
        }
      }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
      if (gatt != null && status == BluetoothGatt.GATT_SUCCESS) {
        Log.v(TAG, "onServicesDiscovered: " + gatt.services)
      } else {
        Log.v(TAG, "onServicesDiscovered: discover services fail")
      }
    }
  }

  init {
    this.context = context.applicationContext
    mBLEHelper = BLEHelper(context, mBleCallback)
    mBLEHelper?.bindService(this)?.let {
      if (it) {
        print("Bind success")
      } else {
        print("Bind failed")
      }
    }
  }
}