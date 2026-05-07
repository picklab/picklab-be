package picklab.backend.activity.domain.enums

enum class RecruitmentEndType(
    val label: String,
) {
    FIXED("날짜 지정"),
    ALWAYS_OPEN("상시모집"),
    CLOSE_ON_HIRE("모집 시 마감"),
}
