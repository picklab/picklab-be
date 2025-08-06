package picklab.backend.activity.domain.enums

enum class ActivityBookmarkSortType(
    val label: String,
) {
    RECENTLY_BOOKMARKED("최근 저장 순"),
    LATEST("최신 순"),
    DEADLINE_ASC("마감 임박 순"),
    DEADLINE_DESC("여유 있는 순"),
    ;

    companion object {
        fun findByType(type: String): ActivityBookmarkSortType =
            ActivityBookmarkSortType.entries.find { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("존재하지 않는 ActivityBookmarkSortType입니다: $type")
    }
}
