package kr.co.byrobot.petroneapi.BLE

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.os.Handler
import android.view.InputDevice.getDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.bluetooth.BluetoothDevice
import android.content.Context.BLUETOOTH_SERVICE
import android.bluetooth.BluetoothManager
import android.content.Context


/**
 * Created by byrobot on 2017. 7. 18..
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class JellybeanScanner : BaseScanner{
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanCallback : PetroneBLEScanCallback? = null

    init {
    }

    constructor(context: Context, callback: PetroneBLEScanCallback ) {
        mScanCallback = callback

        val bluetoothMgr = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothMgr.getAdapter()
    }

    override fun onStartBleScan() {
        val delay = defaultTimeout
        if (mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled()){
            isScanning = mBluetoothAdapter!!.startLeScan(leScanCallback)
        } else {
            mScanCallback?.onBleScanFailed(BLEScanState.BLUETOOTH_OFF)
        }
    }

    override fun onStartBleScan(timeoutMillis: Long) {
        val delay = if (timeoutMillis === 0L) defaultTimeout else timeoutMillis
        if (mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled()){
            isScanning = mBluetoothAdapter!!.startLeScan(leScanCallback)
            timeoutHandler.postDelayed(timeoutRunnable, delay);
        } else {
            mScanCallback?.onBleScanFailed(BLEScanState.BLUETOOTH_OFF)
        }
    }

    override fun onStopBleScan() {
        if( isScanning ) {
            isScanning = false;
            mBluetoothAdapter!!.stopLeScan(leScanCallback)
        }
    }

    override fun onBleScanFailed(scanState: BLEScanState) {
        mScanCallback!!.onBleScanFailed(BLEScanState.SCAN_TIMEOUT)
    }


    private val leScanCallback = object : BluetoothAdapter.LeScanCallback {
        override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
            mScanCallback!!.onBleScan(device!!, rssi, scanRecord!!)
        }
    }
}