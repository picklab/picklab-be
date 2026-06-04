package picklab.backend.participation.application

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.archive.domain.service.ArchiveService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.toPageResponse
import picklab.backend.member.domain.MemberService
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus
import picklab.backend.participation.domain.service.ActivityParticipationService
import picklab.backend.participation.entrypoint.response.ActivityParticipationResultResponse
import picklab.backend.participation.entrypoint.response.ActivityParticipationSummaryResponse
import picklab.backend.review.domain.service.ReviewService

@Component
class ActivityParticipationUseCase(
    private val memberService: MemberService,
    private val activityService: ActivityService,
    private val participationService: ActivityParticipationService,
    private val reviewService: ReviewService,
    private val archiveService: ArchiveService,
) {
    @Transactional
    fun createAppliedParticipation(
        memberId: Long,
        activityId: Long,
    ) {
        val member = memberService.findActiveMember(memberId)
        val activity = activityService.mustFindById(activityId)

        participationService.createAppliedParticipation(member, activity)
    }

    @Transactional
    fun cancelAppliedParticipation(
        memberId: Long,
        activityId: Long,
    ) {
        val participation = participationService.mustFindByMemberIdAndActivityId(memberId, activityId)
        val hasReview = reviewService.existsActiveByActivityIdAndMemberId(activityId, memberId)
        val hasArchive = archiveService.existsActiveByActivityIdAndMemberId(activityId, memberId)
        if (hasReview || hasArchive) {
            throw BusinessException(ErrorCode.CANNOT_CANCEL_ACTIVITY_PARTICIPATION)
        }

        participationService.delete(participation)
    }

    @Transactional
    fun updateApplicationStatus(
        memberId: Long,
        participationId: Long,
        applicationStatus: ApplicationStatus,
    ) {
        val participation = participationService.mustFindByIdAndMemberId(participationId, memberId)
        participationService.updateApplicationStatus(participation, applicationStatus)
    }

    @Transactional
    fun updateProgressStatus(
        memberId: Long,
        participationId: Long,
        progressStatus: ProgressStatus,
    ) {
        val participation = participationService.mustFindByIdAndMemberId(participationId, memberId)
        participationService.updateProgressStatus(participation, progressStatus)
    }

    @Transactional(readOnly = true)
    fun getResults(
        memberId: Long,
        applicationStatuses: List<ApplicationStatus>?,
        page: Int,
        size: Int,
    ): PageResponse<ActivityParticipationResultResponse> {
        memberService.findActiveMember(memberId)
        val pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending())

        return participationService
            .findResults(memberId, applicationStatuses, pageable)
            .toPageResponse { ActivityParticipationResultResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getSummary(memberId: Long): ActivityParticipationSummaryResponse {
        memberService.findActiveMember(memberId)

        return ActivityParticipationSummaryResponse(
            appliedCount = participationService.countAll(memberId),
            acceptedCount = participationService.countByApplicationStatus(memberId, ApplicationStatus.ACCEPTED),
            rejectedCount = participationService.countByApplicationStatus(memberId, ApplicationStatus.REJECTED),
            completedCount = participationService.countByProgressStatus(memberId, ProgressStatus.COMPLETED),
        )
    }
}
