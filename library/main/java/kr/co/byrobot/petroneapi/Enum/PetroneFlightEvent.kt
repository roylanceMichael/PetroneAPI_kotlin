package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 9. 27..
 */
enum class PetroneFlightEvent(val event: Byte) {
    NONE                (0),
    TakeOff             (1),
    FlipFront           (2),
    FlipRear            (3),
    FlipLeft            (4),
    FlipRight           (5),
    Stop                (6),
    Landing             (7),
    TurnOver            (8),
    Shot                (9),
    UnderAttack         (10),
    Square              (11),
    CircleLeft          (12),
    CircleRight         (13),
    Rotate180           (14),
    EndOfType           (15),
}