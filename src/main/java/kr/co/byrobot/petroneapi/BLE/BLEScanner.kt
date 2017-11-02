package kr.co.byrobot.petroneapi.BLE

import android.os.Build
import android.content.Context


/**
 * Created by byrobot on 2017. 7. 18..
 */
class BLEScanner(context: Context, callback: PetroneBLEScanCallback) {
  var bleScanner: BaseScanner = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    LollipopScanner(callback)
  } else {
    JellybeanScanner(context, callback)
  }

  fun isScanning(): Boolean {
    return bleScanner.isScanning
  }

  fun startBleScan() {
    bleScanner.onStartBleScan()
  }

  fun startBleScan(timeoutMillis: Long) {
    bleScanner.onStartBleScan(timeoutMillis)
  }

  fun stopBleScan() {
    bleScanner.onStopBleScan()
  }

}