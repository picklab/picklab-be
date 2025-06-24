package picklab.backend.archive.domain.enums

enum class ProgressStatus(
    val label: String,
) {
    IN_PROGRESSING("진행중"),
    COMPLETED("수료 완료"),
    DROPPED("중도 포기"),
}
