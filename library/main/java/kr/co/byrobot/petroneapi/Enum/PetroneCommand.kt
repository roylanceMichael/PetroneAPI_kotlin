package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 9. 26..
 */
enum class PetroneCommand(val command: Byte) {
    NONE                (0),
    ModePetrone         (0x10),
    Coordinate          (0x20),
    Trim                (0x21),
    FlightEvent         (0x22),
    DriveEvent          (0x23),
    Stop                (0x24),
    ResetHeading        (0x50),
    ClearGyroBiasAndTrim(0x51),
    ClearTrim           (0x52),
    Request             (0x90.toByte()),
    EndOfType           (0x91.toByte()),
}