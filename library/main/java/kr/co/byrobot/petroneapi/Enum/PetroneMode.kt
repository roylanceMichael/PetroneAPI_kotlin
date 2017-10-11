package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 6. 15..
 */
enum class PetroneMode ( val mode: Byte) {
    NONE        (0x00),     // Disconnect from petrone
    FLIGHT      (0x10),     // Flight mode with guard
    FLIGHT_NOGUARD(0x11),   // Flight mode without guard
    FLIGHT_FPV  (0x12),     // Flight mode with FPV kit
    DRIVE       (0x20),     // Drive mode
    DRIVE_FPV   (0x21),     // Drive mode w ith FPV kit
    TEST        (0x30);     // Drive Test

    fun isFlight() : Boolean {
        return this == FLIGHT || this == FLIGHT_NOGUARD || this == FLIGHT_FPV
    }

    fun isDrive() : Boolean {
        return this == DRIVE || this == DRIVE_FPV || this == TEST
    }

    fun isFPV() : Boolean {
        return this == DRIVE_FPV || this == FLIGHT_FPV
    }


    companion object {
        fun fromByte(code: Byte): PetroneMode {
            when (code.toInt()) {
                0x10 -> return PetroneMode.FLIGHT
                0x11 -> return PetroneMode.FLIGHT_NOGUARD
                0x12 -> return PetroneMode.FLIGHT_FPV
                0x20 -> return PetroneMode.DRIVE
                0x21 -> return PetroneMode.DRIVE_FPV
                0x30 -> return PetroneMode.TEST
                else -> return PetroneMode.NONE
            }
        }
    }
}