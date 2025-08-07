package picklab.backend.member.domain.entity

import jakarta.persistence.*
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.common.model.BaseEntity

@Entity
@Table(
    name = "member_activity_view_history",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_member_activity_view",
            columnNames = ["member_id", "activity_id"],
        ),
    ],
)
class MemberActivityViewHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity,
) : BaseEntity()
