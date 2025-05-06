package picklab.backend.activity.domain

enum class PostType(
    val label: String,
) {
    EXTRACURRICULAR("대외활동"),
    COMPETITION("공모전/해커톤"),
    SEMINAR("세미나"),
    EDUCATION("교육"),
}
