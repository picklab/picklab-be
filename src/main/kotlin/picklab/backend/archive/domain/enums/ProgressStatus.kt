package picklab.backend.archive.domain.enums

enum class ProgressStatus(
    val label: String,
) {
    COMPLETED("수료 완료"),
    DROPPED("중도 포기"),
}
