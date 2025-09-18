package picklab.backend.review.entrypoint.mapper

import picklab.backend.common.model.MemberPrincipal
import picklab.backend.review.application.query.model.ActivityReviewListView
import picklab.backend.review.application.query.model.MyReviewListView
import picklab.backend.review.entrypoint.response.ActivityReviewResponse
import picklab.backend.review.entrypoint.response.MyReviewsResponse

fun MyReviewListView.toResponse(): MyReviewsResponse =
    MyReviewsResponse(
        id = id,
        title = title,
        organizer = organizer,
        activityType = activityType,
        createdAt = createdAt,
        approvalStatus = approvalStatus,
    )

fun ActivityReviewListView.toResponse(member: MemberPrincipal?): ActivityReviewResponse {
    val isLoggedIn = member != null

    return ActivityReviewResponse(
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
}
