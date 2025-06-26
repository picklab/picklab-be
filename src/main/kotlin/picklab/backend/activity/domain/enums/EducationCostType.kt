package picklab.backend.activity.domain.enums

enum class EducationCostType(
    val label: String,
) {
    FREE("무료"),
    FULLY_GOVERNMENT("전액 국비지원"),
    PARTIALLY_GOVERNMENT("일부 국비지원"),
    PAID("유료"),
    ;

    companion object {
        fun findByType(type: String): EducationCostType =
            EducationCostType.entries.find { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("존재하지 않는 EducationCost입니다: $type")
    }
}
