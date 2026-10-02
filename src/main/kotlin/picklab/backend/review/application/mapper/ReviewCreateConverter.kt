package picklab.backend.review.application.mapper

import picklab.backend.activity.domain.entity.Activity
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.member.domain.entity.Member
import picklab.backend.review.application.model.ReviewCreateCommand
import picklab.backend.review.domain.entity.Review
import picklab.backend.review.domain.enums.ReviewApprovalStatus

fun ReviewCreateCommand.toEntity(
    approvalStatus: ReviewApprovalStatus,
    objectKey: String?,
    member: Member,
    activity: Activity,
    jobCategory: JobCategory,
): Review =
    Review(
        overallScore = this.overallScore,
        infoScore = this.infoScore,
        difficultyScore = this.difficultyScore,
        benefitScore = this.benefitScore,
        summary = this.summary,
        strength = this.strength,
        weakness = this.weakness,
        tips = this.tips,
        jobRelevanceScore = this.jobRelevanceScore,
        objectKey = objectKey,
        reviewApprovalStatus = approvalStatus,
        member = member,
        activity = activity,
        jobCategory = jobCategory,
    )
