package kr.co.byrobot.petroneapi.BLE

import android.os.Handler

/**
 * Created by byrobot on 2017. 9. 20..
 */
abstract class BaseScanner {
    val defaultTimeout = (10 * 1000).toLong()
    var isScanning: Boolean = false

    abstract fun onStartBleScan()
    abstract fun onStartBleScan(timeoutMillis: Long)

    abstract fun onStopBleScan()

    abstract fun onBleScanFailed(scanState: BLEScanState)

    protected var timeoutHandler = Handler()
    protected var timeoutRunnable: Runnable = Runnable {
        onStopBleScan()
        onBleScanFailed(BLEScanState.SCAN_TIMEOUT)
    }
}