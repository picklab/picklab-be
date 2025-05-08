package picklab.backend.activity.domain

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
}
