package picklab.backend.review.application.mapper

import picklab.backend.review.application.query.model.ActivityReviewListItem
import picklab.backend.review.entrypoint.response.ActivityReviewResponse

fun ActivityReviewListItem.toResponse(isLoggedIn: Boolean): ActivityReviewResponse =
    ActivityReviewResponse(
        id = id,
        overallScore = overallScore,
        infoScore = infoScore,
        difficultyScore = difficultyScore,
        benefitScore = benefitScore,
        jobGroup = jobGroup,
        jobDetail = jobDetail,
        participationDate = participationDate,
        progressStatus = progressStatus,
        summary = summary.takeIf { isLoggedIn },
        strength = strength.takeIf { isLoggedIn },
        weakness = weakness.takeIf { isLoggedIn },
        tips = tips.takeIf { isLoggedIn },
    )
