package picklab.backend.highschool.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "high_school")
class HighSchool(
    @Column(name = "name", nullable = false, length = 100)
    @Comment("고등학교명")
    val name: String,
    @Column(name = "is_active", nullable = false)
    @Comment("검색 노출 여부")
    val isActive: Boolean = true,
) : BaseEntity()
