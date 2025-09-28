package picklab.backend.review.application.mapper

import picklab.backend.review.application.query.model.MyReviewDetailView
import picklab.backend.review.domain.entity.Review

/**
 * [Review] → [MyReviewDetailView]
 *
 * 주의: 사용 시 반드시 영속성 컨텍스트 범위 고려가 필요합니다.
 *
 * @throws org.hibernate.LazyInitializationException 영속성 컨텍스트 밖에서 LAZY 필드(`jobCategory`) 접근 시
 */
fun Review.toDetailView(): MyReviewDetailView =
    MyReviewDetailView(
        jobGroup = this.jobCategory.jobGroup,
        jobDetail = this.jobCategory.jobDetail,
        overallScore = this.overallScore,
        infoScore = this.infoScore,
        difficultyScore = this.difficultyScore,
        benefitScore = this.benefitScore,
        jobRelevanceScore = this.jobRelevanceScore,
        summary = this.summary,
        strength = this.strength,
        weakness = this.weakness,
        tips = this.tips,
        url = this.url,
    )
