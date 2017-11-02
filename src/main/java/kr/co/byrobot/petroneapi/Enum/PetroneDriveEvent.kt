package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 9. 27..
 */
enum class PetroneDriveEvent(val event: Byte) {
    NONE                (0),
    Stop                (1),
    Shot                (2),
    UnderAttack         (3),
    Square              (4),
    CircleLeft          (5),
    CircleRight         (6),
    Rotate90Left        (7),
    Rotate90Right       (8),
    Rotate180           (9),
    Rotate3600          (10),
    EndOfType           (11),
}