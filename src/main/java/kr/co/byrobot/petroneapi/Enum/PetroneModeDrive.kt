package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 6. 15..
 */
enum class PetroneModeDrive ( val status: Byte) {
    NONE (0),       // Disconnect from petrone
    READY (1),      // READY
    START (2),      // START DRIVE
    DRIVE (3),      // DRIVING
    STOP (4),       // STOP DRIVE
    ACCIDENT (5),   // ACCIDENT
    ERROR (6);      // STATUS ERROR

    companion object {
        fun fromByte(code: Byte): PetroneModeDrive {
            when (code.toInt()) {
                1 -> return PetroneModeDrive.READY
                2 -> return PetroneModeDrive.START
                3 -> return PetroneModeDrive.DRIVE
                4 -> return PetroneModeDrive.STOP
                5 -> return PetroneModeDrive.ACCIDENT
                6 -> return PetroneModeDrive.ERROR
                else -> return PetroneModeDrive.NONE
            }
        }
    }
}