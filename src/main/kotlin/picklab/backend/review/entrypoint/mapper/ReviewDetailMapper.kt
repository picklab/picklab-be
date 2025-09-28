package picklab.backend.review.entrypoint.mapper

import picklab.backend.review.application.query.model.MyReviewDetailView
import picklab.backend.review.entrypoint.response.MyReviewResponse

fun MyReviewDetailView.toResponse(): MyReviewResponse =
    MyReviewResponse(
        jobGroup = this.jobGroup,
        jobDetail = this.jobDetail,
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
