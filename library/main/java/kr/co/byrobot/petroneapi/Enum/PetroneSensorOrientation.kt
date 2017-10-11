package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 9. 22..
 */
enum class PetroneSensorOrientation ( val status: Byte)  {
    NONE            (0),
    NORMAL          (1),
    REVERSE_START   (2),
    REVERSED        (3),
    EndOfType       (4);
    companion object {
        fun fromByte(code: Byte): PetroneSensorOrientation {
            when (code.toInt()) {
                0 -> return PetroneSensorOrientation.NONE
                1 -> return PetroneSensorOrientation.NORMAL
                2 -> return PetroneSensorOrientation.REVERSE_START
                3 -> return PetroneSensorOrientation.REVERSED
                else -> return PetroneSensorOrientation.EndOfType
            }
        }
    }
}