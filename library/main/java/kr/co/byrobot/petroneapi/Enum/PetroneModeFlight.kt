package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 6. 15..
 */
enum class PetroneModeFlight ( val status: Byte) {
    NONE (0),       // Disconnect from petrone
    READY (1),      // READY
    TAKEOFF (2),    // TAKE OFF STATUS
    FLIGHT (3),     // FLIGHT
    FLIP (4),       // FLIP STATUS
    STOP (5),       // STOP
    LANDING (6),    // LANDING
    REVERSE (7),    // REVERSE PETRONE
    ACCIDENT (8),   // ACCIDENT
    ERROR (9);      // STATUS ERROR

    companion object {
        fun fromByte(code: Byte): PetroneModeFlight {
            when (code.toInt()) {
                1 -> return PetroneModeFlight.READY
                2 -> return PetroneModeFlight.TAKEOFF
                3 -> return PetroneModeFlight.FLIGHT
                4 -> return PetroneModeFlight.FLIP
                5 -> return PetroneModeFlight.STOP
                6 -> return PetroneModeFlight.LANDING
                7 -> return PetroneModeFlight.REVERSE
                8 -> return PetroneModeFlight.ACCIDENT
                9 -> return PetroneModeFlight.ERROR
                else -> return PetroneModeFlight.NONE
            }
        }
    }
}