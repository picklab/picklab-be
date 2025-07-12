package picklab.backend.participation.domain.enums

enum class ApplicationStatus(
    val label: String,
) {
    APPLIED("지원 완료"),
    ACCEPTED("최종 합격"),
    REJECTED("불합격"),
}
