package picklab.backend.review.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.common.model.SoftDeleteEntity
import picklab.backend.member.domain.Member

@Entity
@Table(name = "review")
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
    var tips: String,
    @Column(name = "job_relevance_score", nullable = false)
    @Comment("직무 연관성 점수")
    var jobRelevanceScore: Int,
    @Column(name = "url", nullable = false)
    @Comment("인증 자료 URL")
    var url: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity,
) : SoftDeleteEntity()
