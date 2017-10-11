package kr.co.byrobot.petroneapi.Enum

/**
 * Created by byrobot on 2017. 9. 26..
 */

enum class PetroneLightMode(val mode: Byte) {
    NONE                (0),

    WaitingForConnect   (0x01),  ///< Waiting for connect
    Connected           (0x02),

    EyeNone             (0x10),
    EyeHold             (0x11),            ///< Eye light hold
    EyeMix              (0x12),             ///< Eye light color change
    EyeFlicker          (0x13),         ///< Eye light flickering
    EyeFlickerDouble    (0x14),   ///< Eye light flickering 2times
    EyeDimming          (0x15),          ///< Eye light dimming

    ArmNone             (0x40),
    ArmHold             (0x41),            ///< 지정한 색상을 계속 켬
    ArmMix              (0x42),             ///< 순차적으로 LED 색 변경
    ArmFlicker          (0x43),         ///< 깜빡임
    ArmFlickerDouble    (0x44),   ///< 깜빡임(두 번 깜빡이고 깜빡인 시간만큼 꺼짐)
    ArmDimming          (0x45),         ///< 밝기 제어하여 천천히 깜빡임
    ArmFlow             (0x46),            ///< 앞에서 뒤로 흐름
    ArmFlowReverse      (0x47),      ///< 뒤에서 앞으로 흐름

    EndOfType           (0x48);
}