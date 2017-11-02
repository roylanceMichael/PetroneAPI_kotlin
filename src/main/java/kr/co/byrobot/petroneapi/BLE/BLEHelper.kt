package kr.co.byrobot.petroneapi.BLE

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.content.ServiceConnection
import android.os.Messenger
import java.util.*
import android.content.ComponentName
import android.os.IBinder
import android.os.Message
import android.os.RemoteException
import kr.co.byrobot.petroneapi.BLE.BLEHelper.OnBindListener
import android.os.Bundle
import android.content.Intent
import android.util.Log
import kr.co.byrobot.petroneapi.PetroneConnectionCallback


/**
 * Created by byrobot on 2017. 9. 15..
 */
class BLEHelper : ServiceConnection, AppHandler.HandleMessageListener<BLEHelper> {
    private val TAG = BLEHelper::class.java!!.getName()
    private val connectTimeout: Long = 15000
    var mState: BLEConnectState = BLEConnectState.INITIALED
    var context : Context? = null
    val mCallbacks: Map<UUID, PetroneBLECallback> = HashMap()

    protected  var mBLECallback : PetroneBLECallback? = null
    private var appHandler: AppHandler<BLEHelper>? = null

    private var mRecvMessenger : Messenger? = null
    private var mSendMessenger : Messenger? = null

    protected var mBindListener : OnBindListener? = null
    protected var mGatt : BluetoothGatt? = null

    var mConnectTimeout : TimeoutCallback? = null
    var mConnCallback : PetroneConnectionCallback? = null

    private var onScan: ((ssid:String, name:String, rssi:Int) -> Unit)? = null
    private var onScanFailure : ((error:String) -> Unit)? = null

    constructor(context:Context) {
        this.context = context
        appHandler = AppHandler<BLEHelper>(this,this)
        mRecvMessenger = Messenger(appHandler)
    }

    constructor(context:Context, bleCallback : PetroneBLECallback) : this(context) {
        this.mBLECallback = bleCallback
    }

    fun bindService(bindListener: OnBindListener): Boolean {
        this.mBindListener = bindListener
        return context?.getApplicationContext()!!.bindService(
                Intent(context, BLEService::class.java), this, Context.BIND_AUTO_CREATE)
    }
    fun unbindService() {
        context?.getApplicationContext()!!.unbindService(this);
    }

    fun connectDevice(mac: String, connectCallback: PetroneConnectionCallback): Boolean {
        if (mState.isServiceDiscovered()) {
            connectCallback.onConnected()
            return true
        } else {
            val msg = Message.obtain(null, BLEConstants.MSG_CONTROL_ID_CONNECT_MAC.code)
            msg.obj = mac
            this.mConnCallback = connectCallback
            if (mSendMessenger != null) {
                try {
                    appHandler?.postDelayed(mConnectTimeout, connectTimeout)
                    mSendMessenger?.send(msg)
                    return true
                } catch (e: RemoteException) {
                }

            }
            return false
        }
    }

    fun startScanDevice(onScan: (ssid:String, name:String, rssi:Int) -> Unit, onScanFailure : (error:String) -> Unit): Boolean {
        this.onScan = onScan
        this.onScanFailure = onScanFailure

        return sendMsgAndSubscribe(BLEConstants.MSG_CONTROL_ID_START_SCAN.code)
    }

    fun stopScanDevice(): Boolean {
        return sendMsgWithoutSubscribe(BLEConstants.MSG_CONTROL_ID_STOP_SCAN.code)
    }

    fun disconnectDevice(): Boolean {
        return sendMsgWithoutSubscribe(BLEConstants.MSG_CONTROL_ID_UNREGISTER.code)
    }

    fun writeCharacteristic(values: ByteArray): Boolean {
        val msg = Message.obtain(null, BLEConstants.MSG_CONTROL_ID_WRITE_CHARACTERISTIC.code)
        if (msg != null && mSendMessenger != null) {
            msg!!.obj = values
            try {
                msg.replyTo = mRecvMessenger;
                mSendMessenger!!.send(msg)
                return true
            } catch (e: RemoteException) {
                Log.w(TAG, "Lost connection to service" + e.toString())
            }

        }
        return false
    }
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        mSendMessenger = Messenger(service)
        sendMsgAndSubscribe(BLEConstants.MSG_CONTROL_ID_REGISTER.code)

        mConnectTimeout = object : TimeoutCallback() {
            override fun onTimeout() {
                if (mConnCallback != null) {
                    mConnCallback?.onError("Connect Timeout")
                }
                stopScanDevice()
            }
        }


        if (mBindListener != null) {
            mBindListener?.onServiceConnected()
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        mSendMessenger = null
    }


    fun isBinded(): Boolean {
        return mSendMessenger != null
    }


    fun release() {
        sendMsgAndSubscribe(BLEConstants.MSG_CONTROL_ID_UNREGISTER.code)
        if (isBinded()) {
            try {
                unbindService()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun sendMsgAndSubscribe(msgId: Int): Boolean {
        val msg = Message.obtain(null, msgId)
        if (msg != null && mSendMessenger != null) {
            try {
                msg.replyTo = mRecvMessenger
                mSendMessenger?.send(msg)
                return true
            } catch (e: RemoteException) {
            }

        }
        return false
    }
    fun sendMsgWithoutSubscribe(msgId: Int): Boolean {
        val msg = Message.obtain(null, msgId)
        if (msg != null && mSendMessenger != null) {
            try {
                mSendMessenger?.send(msg)
                return true
            } catch (e: RemoteException) {
            }

        }
        return false
    }
    interface OnBindListener {
        fun onUpdateBLEDevice(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray)
        fun onServiceConnected()
    }


    override fun onHandleMessage(reference: BLEHelper, msg: Message) {
        val data = msg.data
        when (msg.what) {
            BLEConstants.BLE_MSG_ID_CONNECTION_STATE_CHANGED.code -> {
                val newStatus = msg.obj as BLEConnectState
                if (mBLECallback != null) {
                    mBLECallback?.onConnectionStateChange(mState.getCode(), newStatus.getCode())
                }
                mState = newStatus
                if (mConnCallback != null) {
                    if (mState === BLEConnectState.SERVICE_IS_DISCOVERED) {
                        appHandler?.removeCallbacks(mConnectTimeout)
                        mConnCallback?.onConnected()
                    } else if (mState === BLEConnectState.SERVICE_IS_NOT_DISCOVERED) {
                        appHandler?.removeCallbacks(mConnectTimeout)
                        mConnCallback?.onError( "Connect Timeout")
                    } else if (mState === BLEConnectState.DISCONNECTED) {
                        appHandler?.removeCallbacks(mConnectTimeout)
                        mConnCallback?.onError("Connect Timeout")
                        for (callback in mCallbacks.values ) {
                            if (callback != null) {
                                callback!!.onFailed("ble disconnected...")
                            }
                        }
                    }
                }
            }
            BLEConstants.MSG_BLE_ID_CHARACTERISTIC_WRITE.code -> {
                if (data != null && mBLECallback != null) {
                    val uuid = data.getSerializable("C320DF02-7891-11E5-8BCF-FEFF819CDC9F") as UUID
                    mBLECallback?.onCharacteristicWrite(uuid, msg.arg1)
                }
            }
            BLEConstants.MSG_BLE_ID_DESCRIPTOR_WRITE.code -> {
                if (data != null && mBLECallback != null) {
                    val uuid = data.getSerializable("C320DF02-7891-11E5-8BCF-FEFF819CDC9F") as UUID
                    mBLECallback?.onDescriptorWrite(uuid, msg.arg1)
                }
            }
            BLEConstants.MSG_BLE_ID_CHARACTERISTIC_NOTIFICATION.code -> {
                if (data != null && mBLECallback != null) {
                    val uuid = data.getSerializable("C320DF02-7891-11E5-8BCF-FEFF819CDC9F") as UUID
                    mBLECallback?.onCharacteristicNotification(uuid, msg.obj as ByteArray)
                }
            }
            BLEConstants.MSG_BLE_ID_CHARACTERISTIC_READ.code -> {
                if (data != null && mBLECallback != null) {
                    val uuid = data.getSerializable("C320DF01-7891-11E5-8BCF-FEFF819CDC9F") as UUID
                    mBLECallback?.onCharacteristicRead(uuid, msg.obj as ByteArray)
                }
            }
            BLEConstants.MSG_BLE_ID_DESCRIPTOR_READ.code -> {
                if (data != null && mBLECallback != null) {
                    val uuid = data.getSerializable("C320DF01-7891-11E5-8BCF-FEFF819CDC9F") as UUID
                    mBLECallback?.onDescriptorRead(uuid, msg.obj as ByteArray)
                }
            }
            BLEConstants.MSG_BLE_ID_RELIABLE_WRITE_COMPLETED.code -> {
                if (mBLECallback != null) {
                    mBLECallback?.onReliableWriteCompleted(msg.arg1)
                }
            }
            BLEConstants.MSG_BLE_ID_READ_REMOTE_RSSI.code -> {
                if (mBLECallback != null) {
                    mBLECallback?.onReadRemoteRssi(msg.arg2, msg.arg1)
                }
            }
            BLEConstants.MSG_BLE_ID_MTU_CHANGED.code -> {
                if (mBLECallback != null) {
                    mBLECallback?.onMtuChanged(msg.arg2, msg.arg1)
                }
            }
            BLEConstants.MSG_BLE_ID_SERVICES_DISCOVERED.code -> {
                mGatt = msg.obj as BluetoothGatt
                if (mBLECallback != null) {
                    mBLECallback?.onServicesDiscovered(mGatt!!, msg.arg1)
                }
            }
            BLEConstants.MSG_CONTROL_ID_FIND_DEVICE.code -> {
                var device = msg.obj as BluetoothDevice
                if (device != null) {
                    this.onScan!!.invoke(device.address, device.name, msg.arg1)
                }
            }
            BLEConstants.MSG_CONTROL_ID_SCAN_STOPED.code -> {
                if( this.onScanFailure != null) {
                    this.onScanFailure!!.invoke("Time over")
                }
            }
            else -> {
                Log.v(TAG, "UNKNOWN MSG")
            }
        }
    }
}