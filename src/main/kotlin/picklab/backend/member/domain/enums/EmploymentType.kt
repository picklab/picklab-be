package picklab.backend.member.domain.enums

enum class EmploymentType(
    val label: String,
) {
    NONE("선택 안함"),
    FULL_TIME("정규직"),
    CONTRACT("계약직"),
    INTERN("인턴"),
    PART_TIME("파트타임"),
}
