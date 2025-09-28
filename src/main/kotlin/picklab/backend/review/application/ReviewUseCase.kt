package picklab.backend.review.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.job.domain.service.JobService
import picklab.backend.member.domain.MemberService
import picklab.backend.participation.domain.service.ActivityParticipationService
import picklab.backend.review.application.mapper.toDetailView
import picklab.backend.review.application.mapper.toEntity
import picklab.backend.review.application.model.ActivityReviewListQueryRequest
import picklab.backend.review.application.model.MyReviewListQueryRequest
import picklab.backend.review.application.model.ReviewCreateCommand
import picklab.backend.review.application.model.ReviewUpdateCommand
import picklab.backend.review.application.query.model.ActivityReviewListView
import picklab.backend.review.application.query.model.MyReviewDetailView
import picklab.backend.review.application.query.model.MyReviewListView
import picklab.backend.review.application.service.ReviewOverviewQueryService
import picklab.backend.review.domain.policy.ReviewApprovalDecider
import picklab.backend.review.domain.service.ReviewService

@Component
class ReviewUseCase(
    private val reviewService: ReviewService,
    private val memberService: MemberService,
    private val activityService: ActivityService,
    private val activityParticipationService: ActivityParticipationService,
    private val reviewOverviewQueryService: ReviewOverviewQueryService,
    private val jobService: JobService,
) {
    fun createReview(command: ReviewCreateCommand) {
        val member = memberService.findActiveMember(command.memberId)
        val activity = activityService.mustFindById(command.activityId)
        activityParticipationService.validateCanWriteReview(member.id, activity.id)
        if (reviewService.existsByActivityIdAndMemberId(activity.id, member.id)) {
            throw BusinessException(ErrorCode.ALREADY_EXISTS_REVIEW)
        }
        if (command.jobDetail != null && command.jobDetail.group != command.jobGroup) {
            throw BusinessException(ErrorCode.JOB_CATEGORY_MISMATCH_INPUT)
        }
        val jobCategory =
            jobService.getJobCategoryByGroupAndDetail(command.jobGroup, command.jobDetail)
                ?: throw BusinessException(ErrorCode.JOB_CATEGORY_NOT_FOUND)
        val approvalStatus = ReviewApprovalDecider.decideOnCreate(command.url)
        reviewService.save(command.toEntity(approvalStatus, member, activity, jobCategory))
    }

    @Transactional(readOnly = true)
    fun getMyReview(
        id: Long,
        memberId: Long,
    ): MyReviewDetailView {
        val review = reviewService.mustFindById(id)
        val member = memberService.findActiveMember(memberId)
        if (member.id != review.member.id) {
            throw BusinessException(ErrorCode.CANNOT_READ_REVIEW)
        }
        return review.toDetailView()
    }

    fun getMyReviews(req: MyReviewListQueryRequest): Page<MyReviewListView> {
        val member = memberService.findActiveMember(req.memberId)
        val pageable =
            PageRequest.of(
                req.page - 1,
                req.size,
                Sort.by("createdAt").descending(),
            )

        return reviewOverviewQueryService.findMyReviews(member.id, pageable)
    }

    fun getReviewsByActivity(
        request: ActivityReviewListQueryRequest,
        activityId: Long,
        memberId: Long?,
        page: Int,
        size: Int,
    ): Page<ActivityReviewListView> {
        memberId?.let { memberService.findActiveMember(it) }
        val activity = activityService.mustFindById(activityId)
        val pageable =
            PageRequest.of(
                page - 1,
                size,
                Sort.by("createdAt").descending(),
            )
        return reviewOverviewQueryService
            .findActivityReviews(request, activity.id, pageable)
    }

    @Transactional
    fun updateReview(command: ReviewUpdateCommand) {
        val member = memberService.findActiveMember(command.memberId)
        val review = reviewService.mustFindById(command.id)
        val activity = activityService.mustFindById(command.activityId)
        if (member.id != review.member.id) {
            throw BusinessException(ErrorCode.CANNOT_UPDATE_REVIEW)
        }
        if (command.jobDetail != null && command.jobDetail.group != command.jobGroup) {
            throw BusinessException(ErrorCode.JOB_CATEGORY_MISMATCH_INPUT)
        }
        val jobCategory =
            jobService.getJobCategoryByGroupAndDetail(command.jobGroup, command.jobDetail)
                ?: throw BusinessException(ErrorCode.JOB_CATEGORY_NOT_FOUND)
        val updatedApprovalStatus =
            ReviewApprovalDecider.decideOnUpdate(
                review.url,
                command.url,
                review.activity.id,
                activity.id,
                review.reviewApprovalStatus,
            )

        review.update(
            overallScore = command.overallScore,
            infoScore = command.infoScore,
            difficultyScore = command.difficultyScore,
            benefitScore = command.benefitScore,
            summary = command.summary,
            strength = command.strength,
            weakness = command.weakness,
            tips = command.tips,
            jobRelevanceScore = command.jobRelevanceScore,
            url = command.url,
            approvalStatus = updatedApprovalStatus,
            activity,
            jobCategory = jobCategory,
        )
    }

    @Transactional
    fun deleteReview(
        memberId: Long,
        id: Long,
    ) {
        val review = reviewService.mustFindById(id)
        val member = memberService.findActiveMember(memberId)
        if (member.id != review.member.id) {
            throw BusinessException(ErrorCode.CANNOT_DELETE_REVIEW)
        }
        review.delete()
    }
}
