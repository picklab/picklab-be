package picklab.backend.job.domain

enum class JobGroup(
    val label: String,
) {
    PLANNING("기획"),
    DESIGN("디자인"),
    DEVELOPMENT("개발"),
    MARKETING("마케팅"),
    AI("AI"),
}