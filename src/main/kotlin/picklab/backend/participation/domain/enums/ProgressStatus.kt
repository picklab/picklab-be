package picklab.backend.participation.domain.enums

enum class ProgressStatus(
    val label: String,
) {
    NOT_SELECTED("미선택"),
    IN_PROGRESSING("진행 중"),
    COMPLETED("수료 완료"),
    DROPPED("중도 포기"),
}
