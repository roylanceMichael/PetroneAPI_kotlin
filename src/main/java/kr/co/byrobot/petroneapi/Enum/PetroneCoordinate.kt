package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 9. 22..
 */
enum class PetroneCoordinate ( val status: Byte)  {
    NONE            (0),
    ABSOLUTE        (1),
    RELATIVE        (2),
    FIXED           (3),
    EndOfType       (4);
    companion object {
        fun fromByte(code: Byte): PetroneCoordinate {
            when (code.toInt()) {
                0 -> return PetroneCoordinate.NONE
                1 -> return PetroneCoordinate.ABSOLUTE
                2 -> return PetroneCoordinate.RELATIVE
                3 -> return PetroneCoordinate.FIXED
                else -> return PetroneCoordinate.EndOfType
            }
        }
    }
}