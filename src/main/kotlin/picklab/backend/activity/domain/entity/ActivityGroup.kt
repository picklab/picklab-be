package picklab.backend.activity.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "activity_group")
class ActivityGroup(
    @Column(name = "name", nullable = false)
    @Comment("그룹명")
    var name: String,
    @Column(name = "description")
    @Comment("그룹 설명")
    var description: String,
) : BaseEntity()
