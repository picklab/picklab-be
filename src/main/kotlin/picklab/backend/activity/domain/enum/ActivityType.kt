package picklab.backend.activity.domain.enum

enum class ActivityType(
    val discriminator: String,
    val label: String,
) {
    EXTRACURRICULAR("EXTRACURRICULAR", "대외활동"),
    COMPETITION("COMPETITION", "공모전/해커톤"),
    SEMINAR("SEMINAR", "강연/세미나"),
    EDUCATION("EDUCATION", "교육"),
    ;

    companion object {
        fun findByType(type: String): ActivityType =
            ActivityType.entries.find { it.discriminator == type }
                ?: throw IllegalArgumentException("존재하지 않는 ActivityType입니다: $type")
    }
}
