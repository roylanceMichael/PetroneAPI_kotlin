package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 9. 22..
 */
enum class PetroneModeSystem ( val status: Byte)  {
    NONE            (0),
    BOOT            (1),    ///< System Booting
    WAIT            (2),    ///< Ready for connection
    READY           (3),    ///< Ready for control
    RUNNING         (4),    ///< Running code
    UPDATE          (5),    ///< Firmware Updating.
    UPDATE_COMPLETE (6),    ///< Firmware update complete
    ERROR           (7),    ///< System error
    EndOfType       (8);

    companion object {
        fun fromByte(code: Byte): PetroneModeSystem {
            when (code.toInt()) {
                0 -> return PetroneModeSystem.NONE
                1 -> return PetroneModeSystem.BOOT
                2 -> return PetroneModeSystem.WAIT
                3 -> return PetroneModeSystem.READY
                4 -> return PetroneModeSystem.RUNNING
                5 -> return PetroneModeSystem.UPDATE
                6 -> return PetroneModeSystem.UPDATE_COMPLETE
                7 -> return PetroneModeSystem.ERROR
                else -> return PetroneModeSystem.EndOfType
            }
        }
    }
}