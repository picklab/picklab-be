package picklab.backend.review.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity
import picklab.backend.member.domain.entity.Member

@Entity
@Table(
    name = "review_helpful",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_review_helpful_member_review",
            columnNames = ["member_id", "review_id"],
        ),
    ],
    indexes = [
        Index(
            name = "idx_review_helpful_review_id",
            columnList = "review_id",
        ),
    ],
)
class ReviewHelpful(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원 ID")
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    @Comment("리뷰 ID")
    val review: Review,
) : BaseEntity()
