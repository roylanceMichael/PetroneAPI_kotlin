package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 6. 22..
 */
enum class PetroneDataType ( val datatype : Byte) {
    None                    (0x00),
    Ping                    (0x01),
    Ack                     (0x02),
    Error                   (0x03),
    Request                 (0x04),
    Control                 (0x10),
    Command                 (0x11),
    Command2                (0x12),
    Command3                (0x13),
    LedMode                 (0x20),
    LedMode2                (0x21),
    LedModeCommand          (0x22),
    LedModeCommandIr        (0x23),
    LedModeColor            (0x24),
    LedModeColor2           (0x25),
    LedEvent                (0x26),
    LedEvent2               (0x27),
    LedEventCommand         (0x28),
    LedEventCommandIr       (0x29),
    LedEventColor           (0x2a),
    LedEventColor2          (0x2b),
    LedModeDefaultColor     (0x2c),
    LedModeDefaultColor2    (0x2d),
    Address                 (0x30),
    State                   (0x31),
    Attitude                (0x32),
    GyroBias                (0x33),
    TrimAll                 (0x34),
    TrimFlight              (0x35),
    TrimDrive               (0x36),
    CountFlight             (0x37),
    CountDrive              (0x38),
    IrMessage               (0x40),
    ImuRawAndAngle          (0x50),
    Pressure                (0x51),
    ImageFlow               (0x52),
    Button                  (0x53),
    Battery                 (0x54),
    Motor                   (0x55),
    Temperature             (0x56),
    Range                   (0x57);

    companion object {
        fun newInstance(code: Byte): PetroneDataType {
            when ( code.toInt() ) {
                0x01 -> return PetroneDataType.Ping
                0x02 -> return PetroneDataType.Ack
                0x03 -> return PetroneDataType.Error
                0x04 -> return PetroneDataType.Request
                0x10 -> return PetroneDataType.Control
                0x11 -> return PetroneDataType.Command
                0x12 -> return PetroneDataType.Command2
                0x13 -> return PetroneDataType.Command3
                0x20 -> return PetroneDataType.LedMode
                0x21 -> return PetroneDataType.LedMode2
                0x22 -> return PetroneDataType.LedModeCommand
                0x23 -> return PetroneDataType.LedModeCommandIr
                0x24 -> return PetroneDataType.LedModeColor
                0x25 -> return PetroneDataType.LedModeColor2
                0x26 -> return PetroneDataType.LedEvent
                0x27 -> return PetroneDataType.LedEvent2
                0x28 -> return PetroneDataType.LedEventCommand
                0x29 -> return PetroneDataType.LedEventCommandIr
                0x2a -> return PetroneDataType.LedEventColor
                0x2b -> return PetroneDataType.LedEventColor2
                0x2c -> return PetroneDataType.LedModeDefaultColor
                0x2d -> return PetroneDataType.LedModeDefaultColor2
                0x30 -> return PetroneDataType.Address
                0x31 -> return PetroneDataType.State
                0x32 -> return PetroneDataType.Attitude
                0x33 -> return PetroneDataType.GyroBias
                0x34 -> return PetroneDataType.TrimAll
                0x35 -> return PetroneDataType.TrimFlight
                0x36 -> return PetroneDataType.TrimDrive
                0x37 -> return PetroneDataType.CountFlight
                0x38 -> return PetroneDataType.CountDrive
                0x40 -> return PetroneDataType.IrMessage
                0x50 -> return PetroneDataType.ImuRawAndAngle
                0x51 -> return PetroneDataType.Pressure
                0x52 -> return PetroneDataType.ImageFlow
                0x53 -> return PetroneDataType.Button
                0x54 -> return PetroneDataType.Battery
                0x55 -> return PetroneDataType.Motor
                0x56 -> return PetroneDataType.Temperature
                0x57 -> return PetroneDataType.Range
                else -> return PetroneDataType.None
            }
        }
    }
}