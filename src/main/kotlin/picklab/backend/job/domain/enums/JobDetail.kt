package picklab.backend.job.domain.enums

enum class JobDetail(
    val group: JobGroup,
    val label: String,
) {
    // 기획
    SERVICE_PLANNING(JobGroup.PLANNING, "서비스 기획"),
    BUSINESS_DEVELOPMENT(JobGroup.PLANNING, "사업 개발"),
    DATA_ANALYSIS(JobGroup.PLANNING, "데이터 분석"),
    PM_PO(JobGroup.PLANNING, "PM/PO"),

    // 디자인
    UX_DESIGN(JobGroup.DESIGN, "UX 디자인"),
    UI_DESIGN(JobGroup.DESIGN, "UI 디자인"),
    WEB_DESIGN(JobGroup.DESIGN, "웹디자인"),
    GRAPHIC_DESIGN(JobGroup.DESIGN, "그래픽 디자인"),
    BRAND_DESIGN(JobGroup.DESIGN, "브랜드 디자인"),

    // 개발
    FRONTEND(JobGroup.DEVELOPMENT, "프론트엔드"),
    BACKEND(JobGroup.DEVELOPMENT, "백엔드"),
    FULLSTACK(JobGroup.DEVELOPMENT, "풀스택"),
    SECURITY(JobGroup.DEVELOPMENT, "보안"),
    DEVOPS(JobGroup.DEVELOPMENT, "클라우드/DevOps"),
    IOS(JobGroup.DEVELOPMENT, "iOS"),
    ANDROID(JobGroup.DEVELOPMENT, "안드로이드"),
    BLOCKCHAIN(JobGroup.DEVELOPMENT, "블록체인"),
    GAME(JobGroup.DEVELOPMENT, "게임"),

    // 마케팅
    BRAND_MARKETING(JobGroup.MARKETING, "브랜드 마케팅"),
    CONTENT_MARKETING(JobGroup.MARKETING, "콘텐츠 마케팅"),
    GROWTH_MARKETING(JobGroup.MARKETING, "그로스 마케팅"),
    PERFORMANCE_MARKETING(JobGroup.MARKETING, "퍼포먼스 마케팅"),
    PR(JobGroup.MARKETING, "PR"),

    // AI
    MACHINE_LEARNING(JobGroup.AI, "머신러닝"),
    DEEP_LEARNING(JobGroup.AI, "딥러닝"),
    COMPUTER_VISION(JobGroup.AI, "컴퓨터 비전"),
    NLP(JobGroup.AI, "NLP"),
    DATA_SCIENCE(JobGroup.AI, "데이터"),
    ;

    companion object {
        fun findByType(type: String): JobDetail =
            JobDetail.entries.find { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("존재하지 않는 JobDetail입니다: $type")
    }
}
