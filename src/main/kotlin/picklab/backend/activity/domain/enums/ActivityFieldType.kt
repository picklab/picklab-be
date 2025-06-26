package picklab.backend.activity.domain.enums

enum class ActivityFieldType(
    val label: String,
) {
    SUPPORTERS("서포터즈"),
    MARKETER("마케터"),
    MENTORING("멘토링"),
    PRESS("기자단"),
    OVERSEAS_VOLUNTEER("해외봉사"),
    DOMESTIC_VOLUNTEER("국내봉사단"),
    ;

    companion object {
        fun findByType(type: String): ActivityFieldType =
            ActivityFieldType.entries.find { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("존재하지 않는 ActivityFieldType입니다: $type")
    }
}
