package kr.co.byrobot.openapi.BLE

/**
 * Created by byrobot on 2017. 9. 15..
 */

interface PetroneBLEConnectCallback {
    fun onConnectSuccess()
    fun onConnectFailed(code: Int, message: String)
}