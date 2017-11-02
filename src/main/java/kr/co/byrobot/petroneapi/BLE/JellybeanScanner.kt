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
class JellybeanScanner(context: Context, callback: PetroneBLEScanCallback) : BaseScanner() {
  private var mBluetoothAdapter: BluetoothAdapter? = null
  private var mScanCallback: PetroneBLEScanCallback? = callback

  override fun onStartBleScan() {
    mBluetoothAdapter?.let {
      if (it.isEnabled()) {
        isScanning = it.startLeScan(leScanCallback)
      } else {
        mScanCallback?.onBleScanFailed(BLEScanState.BLUETOOTH_OFF)
      }
    }
  }

  override fun onStartBleScan(timeoutMillis: Long) {
    val delay = if (timeoutMillis == 0L) defaultTimeout else timeoutMillis
    mBluetoothAdapter?.let {
      if (it.isEnabled) {
        isScanning = it.startLeScan(leScanCallback)
        timeoutHandler.postDelayed(timeoutRunnable, delay);
      } else {
        mScanCallback?.onBleScanFailed(BLEScanState.BLUETOOTH_OFF)
      }
    }

  }

  override fun onStopBleScan() {
    if (isScanning) {
      isScanning = false;
      mBluetoothAdapter?.stopLeScan(leScanCallback)
    }
  }

  override fun onBleScanFailed(scanState: BLEScanState) {
    mScanCallback?.onBleScanFailed(BLEScanState.SCAN_TIMEOUT)
  }


  private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
    device?.let {
      d
      scanRecord?.let {
        sr
        mScanCallback?.onBleScan(d, rssi, sr)
      }
    }
  }

  init {
    val bluetoothMgr = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    mBluetoothAdapter = bluetoothMgr.adapter
  }
}