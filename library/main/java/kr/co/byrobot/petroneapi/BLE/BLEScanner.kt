package kr.co.byrobot.petroneapi.BLE

import android.os.Build
import android.content.Context


/**
 * Created by byrobot on 2017. 7. 18..
 */
class BLEScanner {
    var bleScanner: BaseScanner? = null

    init {
    }

    constructor(context: Context, callback: PetroneBLEScanCallback ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleScanner = LollipopScanner(callback)
        } else {
            bleScanner = JellybeanScanner(context, callback)
        }
    }


    fun isScanning(): Boolean {
        return bleScanner!!.isScanning
    }

    fun startBleScan() {
        bleScanner!!.onStartBleScan()
    }

    fun startBleScan(timeoutMillis: Long) {
        bleScanner!!.onStartBleScan(timeoutMillis)
    }

    fun stopBleScan() {
        bleScanner!!.onStopBleScan()
    }
}