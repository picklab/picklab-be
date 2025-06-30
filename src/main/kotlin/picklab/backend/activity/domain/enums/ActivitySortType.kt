package picklab.backend.activity.domain.enums

enum class ActivitySortType(
    val label: String,
) {
    LATEST("최신 순"),
    DEADLINE_ASC("마감 임박 순"),
    DEADLINE_DESC("여유 있는 순"),
    ;

    companion object {
        fun findByType(type: String): ActivitySortType =
            ActivitySortType.entries.find { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("존재하지 않는 ActivitySortType입니다: $type")
    }
}
