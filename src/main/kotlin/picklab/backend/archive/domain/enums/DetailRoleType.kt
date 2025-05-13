package picklab.backend.archive.domain.enums

enum class DetailRoleType(
    val group: RoleType,
    val label: String,
) {
    // 기획
    SERVICE_PLANNING(RoleType.PLANNING, "서비스 기획"),
    BUSINESS_DEVELOPMENT(RoleType.PLANNING, "사업 개발"),
    DATA_ANALYSIS(RoleType.PLANNING, "데이터 분석"),
    PM_PO(RoleType.PLANNING, "PM/PO"),
    PLANNING_ETC(RoleType.PLANNING, "기타"),

    // 디자인
    UX_DESIGN(RoleType.DESIGN, "UX 디자인"),
    UI_DESIGN(RoleType.DESIGN, "UI 디자인"),
    WEB_DESIGN(RoleType.DESIGN, "웹디자인"),
    GRAPHIC_DESIGN(RoleType.DESIGN, "그래픽 디자인"),
    BRAND_DESIGN(RoleType.DESIGN, "브랜드 디자인"),
    DESIGN_ETC(RoleType.DESIGN, "기타"),

    // 개발
    FRONTEND(RoleType.DEVELOPMENT, "프론트엔드"),
    BACKEND(RoleType.DEVELOPMENT, "백엔드"),
    FULLSTACK(RoleType.DEVELOPMENT, "풀스택"),
    SECURITY(RoleType.DEVELOPMENT, "보안"),
    DEVOPS(RoleType.DEVELOPMENT, "클라우드/DevOps"),
    IOS(RoleType.DEVELOPMENT, "iOS"),
    ANDROID(RoleType.DEVELOPMENT, "안드로이드"),
    BLOCKCHAIN(RoleType.DEVELOPMENT, "블록체인"),
    GAME(RoleType.DEVELOPMENT, "게임"),
    DEVELOPMENT_ETC(RoleType.DEVELOPMENT, "기타"),

    // 마케팅
    BRAND_MARKETING(RoleType.MARKETING, "브랜드 마케팅"),
    CONTENT_MARKETING(RoleType.MARKETING, "콘텐츠 마케팅"),
    GROWTH_MARKETING(RoleType.MARKETING, "그로스 마케팅"),
    PERFORMANCE_MARKETING(RoleType.MARKETING, "퍼포먼스 마케팅"),
    PR(RoleType.MARKETING, "PR"),
    MARKETING_ETC(RoleType.MARKETING, "기타"),

    // AI
    MACHINE_LEARNING(RoleType.AI, "머신러닝"),
    DEEP_LEARNING(RoleType.AI, "딥러닝"),
    COMPUTER_VISION(RoleType.AI, "컴퓨터 비전"),
    NLP(RoleType.AI, "NLP"),
    DATA_SCIENCE(RoleType.AI, "데이터"),
    AI_ETC(RoleType.AI, "기타"),
}
