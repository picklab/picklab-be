package picklab.backend.activity.domain.enums

enum class EducationFormatType(
    val label: String,
) {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    ALL("온라인/오프라인"),
    ;

    companion object {
        fun findByType(type: String): EducationFormatType =
            EducationFormatType.entries.find { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("존재하지 않는 EducationFormatType입니다: $type")
    }
}
