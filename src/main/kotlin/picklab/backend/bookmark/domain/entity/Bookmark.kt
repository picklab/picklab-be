package picklab.backend.bookmark.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.common.model.BaseEntity
import picklab.backend.member.domain.entity.Member

@Entity
@Table(name = "bookmark")
class Bookmark(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원 ID")
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    @Comment("활동 ID")
    val activity: Activity,
) : BaseEntity()
