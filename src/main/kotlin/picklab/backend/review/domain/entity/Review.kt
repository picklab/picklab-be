package picklab.backend.review.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.common.model.SoftDeleteEntity
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.member.domain.entity.Member
import picklab.backend.review.domain.enums.ReviewApprovalStatus

@Entity
@Table(name = "review")
@SQLDelete(sql = "UPDATE review SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class Review(
    @Column(name = "overall_score", nullable = false)
    @Comment("총 평점")
    var overallScore: Int,
    @Column(name = "info_score", nullable = false)
    @Comment("정보 점수")
    var infoScore: Int,
    @Column(name = "difficulty_score", nullable = false)
    @Comment("강도 점수")
    var difficultyScore: Int,
    @Column(name = "benefit_score", nullable = false)
    @Comment("혜택 점수")
    var benefitScore: Int,
    @Column(name = "summary", nullable = false)
    @Comment("한줄 평")
    var summary: String,
    @Column(name = "strength", length = 1000, nullable = false)
    @Comment("장점")
    var strength: String,
    @Column(name = "weakness", length = 1000, nullable = false)
    @Comment("단점")
    var weakness: String,
    @Column(name = "tips", length = 1000)
    @Comment("꿀팁")
    var tips: String? = null,
    @Column(name = "job_relevance_score", nullable = false)
    @Comment("직무 연관성 점수")
    var jobRelevanceScore: Int,
    @Column(name = "url")
    @Comment("인증 자료 URL")
    var url: String? = null,
    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    @Comment("승인 여부 상태(미승인 / 승인 / 승인 중)")
    var reviewApprovalStatus: ReviewApprovalStatus,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    var activity: Activity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_category_id", nullable = false)
    var jobCategory: JobCategory,
) : SoftDeleteEntity() {
    fun update(
        overallScore: Int,
        infoScore: Int,
        difficultyScore: Int,
        benefitScore: Int,
        summary: String,
        strength: String,
        weakness: String,
        tips: String?,
        jobRelevanceScore: Int,
        url: String?,
        approvalStatus: ReviewApprovalStatus,
        activity: Activity,
        jobCategory: JobCategory,
    ) {
        this.overallScore = overallScore
        this.infoScore = infoScore
        this.difficultyScore = difficultyScore
        this.benefitScore = benefitScore
        this.summary = summary
        this.strength = strength
        this.weakness = weakness
        this.tips = tips
        this.jobRelevanceScore = jobRelevanceScore
        this.url = url
        this.reviewApprovalStatus = approvalStatus
        this.activity = activity
        this.jobCategory = jobCategory
    }
}
