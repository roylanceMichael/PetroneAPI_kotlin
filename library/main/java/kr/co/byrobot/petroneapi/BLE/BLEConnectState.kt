package kr.co.byrobot.petroneapi.BLE

/**
 * Created by byrobot on 2017. 9. 13..
 */
enum class BLEConnectState {
    INITIALED               (0, "BLE initialed"),
    SCANNING                (1, "Scanning"),
    CONNECTING              (2, "Connecting"),
    CONNECTED               (3, "Connect"),
    SERVICE_IS_DISCOVERING  (4, "Services discovering"),
    SERVICE_IS_DISCOVERED   (5, "Services discovered"),
    DISCONNECTING           (6, "Disconnecting"),
    DISCONNECTED            (7, "Disconnected"),
    BLUETOOTH_OFF           (8, "Bluetooth_off"),
    SERVICE_IS_NOT_DISCOVERED(9, "Services discover failed");

    private var code:Int
    private var message:String

    constructor(code: Int, message: String) {
        this.code = code
        this.message = message
    }

    fun isBluetoothOff() : Boolean {
        return this.code == BLUETOOTH_OFF.code
    }

    fun isServiceDiscovered() : Boolean {
        return this.code == SERVICE_IS_DISCOVERED.code
    }

    fun isConnecting() : Boolean {
        return this.code > INITIALED.code && this.code < SERVICE_IS_DISCOVERED.code;
    }

    fun needConnect() : Boolean {
        return this.code == INITIALED.code || this.code == DISCONNECTING.code || this.code == DISCONNECTED.code
    }

    fun isConnected() : Boolean {
        return this.code == CONNECTED.code
                || this.code == SERVICE_IS_DISCOVERING.code
                || this.code == SERVICE_IS_DISCOVERED.code;
    }

    fun getCode() : Int {
        return code
    }

    fun getMessage() : String {
        return message
    }

    fun setState(code: Int, message: String) {
        this.code = code
        this.message = message
    }

    companion object {
        fun newInstance(code: Int): BLEConnectState {
            when (code) {
                0 -> return BLEConnectState.INITIALED
                1 -> return BLEConnectState.SCANNING
                2 -> return BLEConnectState.CONNECTING
                3 -> return BLEConnectState.CONNECTED
                4 -> return BLEConnectState.SERVICE_IS_DISCOVERING
                5 -> return BLEConnectState.SERVICE_IS_DISCOVERED
                6 -> return BLEConnectState.DISCONNECTING
                6 -> return BLEConnectState.DISCONNECTED
                6 -> return BLEConnectState.BLUETOOTH_OFF
                else -> return BLEConnectState.SERVICE_IS_NOT_DISCOVERED
            }
        }
    }
}