package kr.co.byrobot.petroneapi.BLE

/**
 * Created by byrobot on 2017. 7. 18..
 */
enum class BLEScanState(private var code: Int, private var message: String) {
  SCAN_TIMEOUT(-2, "SCAN_SUCCESS_TIME_OUT"),
  BLUETOOTH_OFF(-1, "BLUETOOTH_OFF"),
  SCAN_SUCCESS(0, "SCAN_SUCCESS"),
  SCAN_FAILED_ALREADY_STARTED(1, "SCAN_FAILED_ALREADY_STARTED"),
  SCAN_FAILED_APPLICATION_REGISTRATION_FAILED(2, "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED"),
  SCAN_FAILED_INTERNAL_ERROR(3, "SCAN_FAILED_INTERNAL_ERROR"),
  SCAN_FAILED_FEATURE_UNSUPPORTED(4, "SCAN_FAILED_FEATURE_UNSUPPORTED"),
  SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES(5, "SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES");

  fun getCode(): Int {
    return code
  }

  fun getMessage(): String {
    return message
  }

  companion object {
    fun newInstance(code: Int): BLEScanState {
      return when (code) {
        -2 -> BLEScanState.SCAN_TIMEOUT;
        -1 -> BLEScanState.BLUETOOTH_OFF;
        1 -> BLEScanState.SCAN_FAILED_ALREADY_STARTED;
        2 -> BLEScanState.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED;
        3 -> BLEScanState.SCAN_FAILED_INTERNAL_ERROR;
        4 -> BLEScanState.SCAN_FAILED_FEATURE_UNSUPPORTED;
        5 -> BLEScanState.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES;
        else -> BLEScanState.SCAN_SUCCESS;
      }
    }
  }
}