package picklab.backend.review.application

import org.springframework.stereotype.Component
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.MemberService
import picklab.backend.participation.domain.service.ActivityParticipationService
import picklab.backend.review.application.model.ReviewCreateCommand
import picklab.backend.review.domain.policy.ReviewApprovalDecider
import picklab.backend.review.domain.service.ReviewService

@Component
class ReviewUseCase(
    private val reviewService: ReviewService,
    private val memberService: MemberService,
    private val activityService: ActivityService,
    private val activityParticipationService: ActivityParticipationService,
    private val reviewCreateConverter: ReviewCreateConverter,
) {
    fun createReview(command: ReviewCreateCommand) {
        val member = memberService.findActiveMember(command.memberId)
        val activity = activityService.mustFindById(command.activityId)
        activityParticipationService.validateCanWriteReview(member.id, activity.id)
        if (reviewService.existsByActivityIdAndMemberId(activity.id, member.id)) {
            throw BusinessException(ErrorCode.ALREADY_EXISTS_REVIEW)
        }
        val approvalStatus = ReviewApprovalDecider.decide(command.url)
        val review = reviewCreateConverter.toEntity(command, approvalStatus, member, activity)
        reviewService.save(review)
    }
}
