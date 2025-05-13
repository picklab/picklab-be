package picklab.backend.activity.domain.enums

enum class LocationType(
    val label: String,
) {
    ALL("모두"),
    SEOUL_INCHEON("서울/인천"),
    GYEONGGI_GANGWON("경기/강원"),
    DAEJEON_SEJONG_CHUNGNAM("대전/세종/충남"),
    BUSAN_DAEGU_GYEONGSANG("부산/대구/경상"),
    GWANGJU_JEOLLA("광주/전라"),
    JEJU("제주"),
    OVERSEAS("해외"),
}
