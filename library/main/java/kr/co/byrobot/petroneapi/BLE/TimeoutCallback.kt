package kr.co.byrobot.petroneapi.BLE

/**
 * Created by byrobot on 2017. 9. 15..
 */

abstract class TimeoutCallback : Runnable {
    abstract fun onTimeout()
    override fun run() {
        onTimeout()
    }
}