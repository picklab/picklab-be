package picklab.backend.activity.domain.enums

enum class ParticipantType(
    val label: String,
) {
    ALL("대상 제한 없음"),
    UNIVERSITY_STUDENT("대학생"),
    WORKER("직장인/일반인"),
}
