package picklab.backend.review.application

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.PageResponse
import picklab.backend.member.domain.MemberService
import picklab.backend.participation.domain.service.ActivityParticipationService
import picklab.backend.review.application.mapper.toResponse
import picklab.backend.review.application.model.ActivityReviewListQueryRequest
import picklab.backend.review.application.model.MyReviewListQueryRequest
import picklab.backend.review.application.model.ReviewCreateCommand
import picklab.backend.review.application.model.ReviewUpdateCommand
import picklab.backend.review.application.service.ReviewOverviewQueryService
import picklab.backend.review.domain.policy.ReviewApprovalDecider
import picklab.backend.review.domain.service.ReviewService
import picklab.backend.review.entrypoint.response.ActivityReviewResponse
import picklab.backend.review.entrypoint.response.MyReviewResponse
import picklab.backend.review.entrypoint.response.MyReviewsResponse

@Component
class ReviewUseCase(
    private val reviewService: ReviewService,
    private val memberService: MemberService,
    private val activityService: ActivityService,
    private val activityParticipationService: ActivityParticipationService,
    private val reviewCreateConverter: ReviewCreateConverter,
    private val reviewOverviewQueryService: ReviewOverviewQueryService,
) {
    fun createReview(command: ReviewCreateCommand) {
        val member = memberService.findActiveMember(command.memberId)
        val activity = activityService.mustFindById(command.activityId)
        activityParticipationService.validateCanWriteReview(member.id, activity.id)
        if (reviewService.existsByActivityIdAndMemberId(activity.id, member.id)) {
            throw BusinessException(ErrorCode.ALREADY_EXISTS_REVIEW)
        }
        val approvalStatus = ReviewApprovalDecider.decideOnCreate(command.url)
        val review = reviewCreateConverter.toEntity(command, approvalStatus, member, activity)
        reviewService.save(review)
    }

    fun getMyReview(
        id: Long,
        memberId: Long,
    ): MyReviewResponse {
        val review = reviewService.mustFindById(id)
        val member = memberService.findActiveMember(memberId)
        if (member.id != review.member.id) {
            throw BusinessException(ErrorCode.CANNOT_READ_REVIEW)
        }
        return MyReviewResponse.from(review)
    }

    fun getMyReviews(req: MyReviewListQueryRequest): PageResponse<MyReviewsResponse> {
        val member = memberService.findActiveMember(req.memberId)
        val pageable =
            PageRequest.of(
                req.page - 1,
                req.size,
                Sort.by("createdAt").descending(),
            )
        val page = reviewOverviewQueryService.findMyReviews(member.id, pageable)
        val responsePage = page.map { MyReviewsResponse.from(it) }

        return PageResponse.from(responsePage)
    }

    fun getReviewsByActivity(
        request: ActivityReviewListQueryRequest,
        activityId: Long,
        memberId: Long?,
        page: Int,
        size: Int,
    ): PageResponse<ActivityReviewResponse> {
        val member = memberId?.let { memberService.findActiveMember(it) }
        val activity = activityService.mustFindById(activityId)
        val pageable =
            PageRequest.of(
                page - 1,
                size,
                Sort.by("createdAt").descending(),
            )
        val page =
            reviewOverviewQueryService
                .findActivityReviews(request, activity.id, pageable)
        val isLoggedIn = member != null
        val responsePage = page.map { it.toResponse(isLoggedIn) }

        return PageResponse.from(responsePage)
    }

    @Transactional
    fun updateReview(command: ReviewUpdateCommand) {
        val member = memberService.findActiveMember(command.memberId)
        val review = reviewService.mustFindById(command.id)
        val activity = activityService.mustFindById(command.activityId)
        if (member.id != review.member.id) {
            throw BusinessException(ErrorCode.CANNOT_UPDATE_REVIEW)
        }
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
        )
    }
}
