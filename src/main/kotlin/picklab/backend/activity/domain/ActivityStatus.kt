package picklab.backend.activity.domain

enum class ActivityStatus(
    val label: String,
) {
    OPEN("모집 중"),
    CLOSED("마감완료"),
}
