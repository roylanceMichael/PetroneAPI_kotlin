package kr.co.byrobot.petroneapi

import android.bluetooth.*
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import kr.co.byrobot.petroneapi.Packet.PetronePacket
import java.util.*
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.bluetooth.BluetoothGatt
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kr.co.byrobot.petroneapi.BLE.*
import kr.co.byrobot.petroneapi.Packet.PetroneByteArray
import org.jetbrains.anko.doAsync
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask


/**
 * Created by byrobot on 2017. 7. 10..
 */


class PetroneBLE : PetroneNetworkInterface , BLEHelper.OnBindListener{
    private val TAG = PetroneBLE::class.java!!.getName()

    private final val REQUEST_NUM_BLE_ON = 1
    private final val SEND_VALUE_LENGTH = 20

    private var mDeviceAddress : String = ""
    private var mBLEHelper : BLEHelper? = null
    private var context : Context? = null

    private var connected : Boolean = false

    constructor(context: Context) {
        this.context = context.applicationContext
        mBLEHelper = BLEHelper(context!!, mBleCallback)
        if(mBLEHelper!!.bindService(this)) {
            print("Bind success")
        }  else {
            print("Bind failed")
        }
    }

    override fun initWithCallback(callback: PetroneCallback) {

    }
    override fun scan(onScan: (ssid:String, name:String, rssi:Int) -> Unit, onScanFailure : (error:String) -> Unit) {
        if( !mBLEHelper!!.isBinded() ) {
            if( mBLEHelper!!.bindService(this)) {
                Log.v(TAG, "Service bind failed")
            }
        }

        mBLEHelper!!.startScanDevice(onScan, onScanFailure)
    }
    override fun stopScan() {
        mBLEHelper!!.stopScanDevice()
    }

    private var connectionCallback : PetroneConnectionCallback? = null
    override fun connect(target: String, callback: PetroneConnectionCallback) {
        if( callback != null ) {
            connectionCallback = callback
        }

        mBLEHelper?.connectDevice(target, callback);
    }
    override fun isConnected(): Boolean {
        return connected
    }
    override fun getConnectionList(): List<String> {
        var connectList: MutableList<String> = mutableListOf()

        return connectList
    }
    override fun disconnect(target: String) {
        connected = false
        mDeviceAddress = ""
        mBLEHelper?.release()
        mBLEHelper = BLEHelper(context!!, mBleCallback)
        mBLEHelper!!.bindService(this)
    }

    override fun sendPacket(packet: PetronePacket) {
        mBLEHelper!!.writeCharacteristic(packet.encode().toByteArray())
    }

    override fun recvPacket(data: ByteArray) {
        if( connectionCallback != null ) {
            connectionCallback!!.onRecvPacket(data)
        }
    }

    override fun onUpdateBLEDevice(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
    }

    override fun onServiceConnected() {
    }

    private val mBleCallback = object : PetroneBLECallback {
        override fun onFailed(msg: String) { }

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
                Log.v(TAG, "onCharacteristicWrite: " +  status.toString());
            }
        }

        override fun onConnectionStateChange(status: Int, newStatus: Int) {
            val connectState = BLEConnectState.newInstance(newStatus)
            if (connectState.needConnect()) {
                disconnect("")
            } else if( connectState.isConnected() ) {
                if( !connected ) {
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
}