package picklab.backend.archive.domain.enums

enum class WriteStatus(
    val label: String,
) {
    NOT_WRITTEN("미작성"),
    IN_PROGRESS("작성 중"),
    COMPLETED("작성 완료"),
}
