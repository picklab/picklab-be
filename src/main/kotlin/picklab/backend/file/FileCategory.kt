package picklab.backend.file

enum class FileCategory(
    val label: String,
) {
    PROFILE("프로필 이미지"),
    ARCHIVE("아카이브"),
    REVIEW("리뷰"),
    ;

    companion object {
        fun findByType(type: String): FileCategory =
            FileCategory.entries.find { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("존재하지 않는 FileCategory입니다.: $type")
    }
}
