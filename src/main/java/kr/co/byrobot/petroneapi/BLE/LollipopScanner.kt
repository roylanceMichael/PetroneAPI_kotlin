package kr.co.byrobot.petroneapi.BLE

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.Handler

/**
 * Created by byrobot on 2017. 9. 20..
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class LollipopScanner : BaseScanner {
    private var mBluetoothScanner: BluetoothLeScanner? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanCallback : PetroneBLEScanCallback? = null

    init {
    }

    constructor(callback: PetroneBLEScanCallback ) {
        mScanCallback = callback

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter != null) {
            mBluetoothScanner = mBluetoothAdapter!!.bluetoothLeScanner
        }
    }

    override fun onStartBleScan() {
        val delay = defaultTimeout
        if (mBluetoothScanner != null && mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled()) {
            try {
                mBluetoothScanner!!.startScan(scanCallback)
                isScanning = true
            } catch (e: Exception) {
                isScanning = false
            }

            timeoutHandler.postDelayed(timeoutRunnable, delay)
        } else {
            mScanCallback?.onBleScanFailed(BLEScanState.BLUETOOTH_OFF)
        }
    }

    override fun onStartBleScan(timeoutMillis: Long) {
        val delay = if (timeoutMillis === 0L) defaultTimeout else timeoutMillis
        if (mBluetoothScanner != null && mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled()) {
            try {
                mBluetoothScanner!!.startScan(scanCallback)
                isScanning = true
            } catch (e: Exception) {
                isScanning = false
            }

            timeoutHandler.postDelayed(timeoutRunnable, delay)
        }else {
            mScanCallback?.onBleScanFailed(BLEScanState.BLUETOOTH_OFF)
        }
    }

    override fun onStopBleScan() {
        if (isScanning) {
            mBluetoothScanner?.stopScan(scanCallback)
        }
    }

    override fun onBleScanFailed(scanState: BLEScanState) {
        mScanCallback!!.onBleScanFailed(BLEScanState.SCAN_TIMEOUT)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (result!!.getScanRecord() != null) {
                mScanCallback?.onBleScan(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes())
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            if (errorCode != 3 && errorCode != 1) {
                mScanCallback?.onBleScanFailed(BLEScanState.newInstance(errorCode))
            }
        }
    }
}