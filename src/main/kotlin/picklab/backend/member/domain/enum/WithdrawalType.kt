package picklab.backend.member.domain.enum

enum class WithdrawalType(
    val label: String,
) {
    LACK_OF_CONTENT("원하는 활동 정보가 부족해요"),
    LOW_TRUST("리뷰/정보 신뢰도가 낮다고 느꼈어요"),
    HARD_TO_USE("이용 방법이 복잡하거나 불편했어요"),
    USING_OTHER_SERVICE("비슷한 다른 서비스를 이용하고 있어요"),
    TOO_MANY_ERRORS("서비스 오류나 버그가 많았어요"),
    ETC("기타"),
}
