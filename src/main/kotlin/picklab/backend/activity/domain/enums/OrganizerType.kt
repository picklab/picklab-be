package picklab.backend.activity.domain.enums

enum class OrganizerType(
    val label: String,
) {
    LARGE_CORPORATION("대기업"),
    MEDIUM_CORPORATION("중견기업"),
    SMALL_CORPORATION("중소기업"),
    PUBLIC_ORGANIZATION("공공기관/공기관"),
    FOREIGN_CORPORATION("외국계기업"),
    NON_PROFIT("비영리단체/협회/재단"),
    STARTUP("스타트업"),
    FINANCIAL_INSTITUTION("금융권"),
    HOSPITAL("병원"),
    ETC("기타"),
    ;

    companion object {
        fun findByType(type: String): OrganizerType =
            OrganizerType.entries.find { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("존재하지 않는 OrganizerType입니다: $type")
    }
}
