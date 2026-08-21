package picklab.backend.university.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "university")
class University(
    @Column(name = "name", nullable = false, length = 100)
    @Comment("대학교명")
    val name: String,
    @Column(name = "is_active", nullable = false)
    @Comment("검색 노출 여부")
    val isActive: Boolean = true,
) : BaseEntity()
