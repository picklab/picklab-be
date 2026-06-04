package picklab.backend.participation.domain.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.entity.Member
import picklab.backend.participation.domain.entity.ActivityParticipation
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus
import picklab.backend.participation.domain.repository.ActivityParticipationRepository

@Service
class ActivityParticipationService(
    private val participationRepository: ActivityParticipationRepository,
) {
    fun createAppliedParticipation(
        member: Member,
        activity: Activity,
    ): ActivityParticipation {
        if (participationRepository.findByMemberIdAndActivityId(member.id, activity.id) != null) {
            throw BusinessException(ErrorCode.ALREADY_EXISTS_ACTIVITY_PARTICIPATION)
        }

        return participationRepository.save(
            ActivityParticipation(
                applicationStatus = ApplicationStatus.APPLIED,
                progressStatus = ProgressStatus.NOT_SELECTED,
                member = member,
                activity = activity,
            ),
        )
    }

    fun mustFindByIdAndMemberId(
        id: Long,
        memberId: Long,
    ): ActivityParticipation =
        participationRepository
            .findById(id)
            .filter { it.member.id == memberId }
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_PARTICIPATION) }

    fun mustFindByMemberIdAndActivityId(
        memberId: Long,
        activityId: Long,
    ): ActivityParticipation =
        participationRepository.findByMemberIdAndActivityId(memberId, activityId)
            ?: throw BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_PARTICIPATION)

    fun findResults(
        memberId: Long,
        applicationStatuses: List<ApplicationStatus>?,
        pageable: Pageable,
    ): Page<ActivityParticipation> =
        if (applicationStatuses.isNullOrEmpty()) {
            participationRepository.findAllByMemberId(memberId, pageable)
        } else {
            participationRepository.findAllByMemberIdAndApplicationStatusIn(memberId, applicationStatuses, pageable)
        }

    fun countAll(memberId: Long): Long = participationRepository.countByMemberId(memberId)

    fun countByApplicationStatus(
        memberId: Long,
        applicationStatus: ApplicationStatus,
    ): Long = participationRepository.countByMemberIdAndApplicationStatus(memberId, applicationStatus)

    fun countByProgressStatus(
        memberId: Long,
        progressStatus: ProgressStatus,
    ): Long = participationRepository.countByMemberIdAndProgressStatus(memberId, progressStatus)

    fun findCompletedForArchive(
        memberId: Long,
        activityType: ActivityType?,
        sort: Sort,
    ): List<ActivityParticipation> =
        if (activityType == null) {
            participationRepository.findAllByMemberIdAndProgressStatus(memberId, ProgressStatus.COMPLETED, sort)
        } else {
            participationRepository.findAllByMemberIdAndProgressStatusAndActivityActivityType(
                memberId,
                ProgressStatus.COMPLETED,
                activityType.discriminator,
                sort,
            )
        }

    fun updateApplicationStatus(
        participation: ActivityParticipation,
        applicationStatus: ApplicationStatus,
    ) {
        participation.updateApplicationStatus(applicationStatus)
        participationRepository.save(participation)
    }

    fun updateProgressStatus(
        participation: ActivityParticipation,
        progressStatus: ProgressStatus,
    ) {
        if (participation.applicationStatus != ApplicationStatus.ACCEPTED) {
            throw BusinessException(ErrorCode.CANNOT_UPDATE_ACTIVITY_PROGRESS_STATUS)
        }

        participation.updateProgressStatus(progressStatus)
        participationRepository.save(participation)
    }

    fun delete(participation: ActivityParticipation) {
        participation.delete()
        participationRepository.save(participation)
    }

    fun validateCanWriteReview(
        memberId: Long,
        activityId: Long,
    ) {
        val participation = mustFindByMemberIdAndActivityId(memberId, activityId)
        if (!participation.canWriteReview()) {
            throw BusinessException(ErrorCode.CANNOT_WRITE_REVIEW)
        }
    }
}
