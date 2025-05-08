package picklab.backend.activity.domain.enum

enum class DomainType(
    val label: String,
) {
    SAAS("SaaS"),
    WEB("웹"),
    MOBILE("모바일"),
    CLOUD("클라우드"),
    AI("AI"),
    IOT("사물인터넷(IoT)"),
    EDUCATION("교육"),
    FINANCE("금융"),
    GAME_ENTERTAINMENT("게임/엔터테인먼트"),
    LIFE_SCIENCE_HEALTHCARE("생활과학/헬스케어"),
    COMMERCE("커머스"),
    CONTENT_SOCIAL("콘텐츠/소셜"),
    TRANSPORT_LOGISTICS("교통/물류"),
    AR_VR("AR/VR"),
    ELECTRONICS_ROBOTICS("전자기술/로보틱스"),
    BUSINESS_ENTERPRISE("비즈니스/엔터프라이즈"),
    ETC("기타"),
}
