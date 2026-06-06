package picklab.backend.review.entrypoint.mapper

import picklab.backend.common.model.MemberPrincipal
import picklab.backend.review.application.model.ActivityReviewWithHelpful
import picklab.backend.review.application.query.model.MyReviewListView
import picklab.backend.review.entrypoint.response.ActivityReviewResponse
import picklab.backend.review.entrypoint.response.MyReviewsResponse

fun MyReviewListView.toResponse(): MyReviewsResponse =
    MyReviewsResponse(
        id = id,
        title = title,
        organizer = organizer,
        organizerType = organizerType,
        activityType = activityType,
        createdAt = createdAt,
        approvalStatus = approvalStatus,
    )

fun ActivityReviewWithHelpful.toResponse(member: MemberPrincipal?): ActivityReviewResponse {
    val isLoggedIn = member != null
    val review = this.review

    return ActivityReviewResponse(
        id = review.id,
        overallScore = review.overallScore,
        infoScore = review.infoScore,
        difficultyScore = review.difficultyScore,
        benefitScore = review.benefitScore,
        jobGroup = review.jobGroup,
        jobDetail = review.jobDetail,
        participationDate = review.participationDate,
        progressStatus = review.progressStatus,
        helpfulCount = helpfulCount,
        isHelpful = isHelpful,
        summary = review.summary.takeIf { isLoggedIn },
        strength = review.strength.takeIf { isLoggedIn },
        weakness = review.weakness.takeIf { isLoggedIn },
        tips = review.tips.takeIf { isLoggedIn },
    )
}
