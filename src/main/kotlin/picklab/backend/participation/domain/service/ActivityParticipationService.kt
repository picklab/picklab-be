package picklab.backend.participation.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.entity.Member
import picklab.backend.participation.domain.entity.ActivityParticipation
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus
import picklab.backend.participation.domain.repository.ActivityParticipationRepository
import java.time.LocalDate

@Service
class ActivityParticipationService(
    private val participationRepository: ActivityParticipationRepository,
) {
    fun mustFindByMemberIdAndActivityId(
        memberId: Long,
        activityId: Long,
    ): ActivityParticipation =
        participationRepository.findByMemberIdAndActivityId(memberId, activityId)
            ?: throw BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_PARTICIPATION)

    fun validateCanWriteReview(
        memberId: Long,
        activityId: Long,
    ) {
        val participation = mustFindByMemberIdAndActivityId(memberId, activityId)
        if (!participation.canWriteReview()) {
            throw BusinessException(ErrorCode.CANNOT_WRITE_REVIEW)
        }
    }

    /**
     * 활동에 지원합니다. ApplicationStatus.APPLIED 상태의 새로운 지원 데이터를 만듭니다.
     */
    @Transactional
    fun applyToActivity(
        member: Member,
        activity: Activity,
    ): ActivityParticipation {
        val participation =
            ActivityParticipation(
                applicationStatus = ApplicationStatus.APPLIED,
                progressStatus = ProgressStatus.IN_PROGRESSING,
                member = member,
                activity = activity,
            )

        return participationRepository.save(participation)
    }

    /**
     * 지원가능한 활동인지 파악합니다.
     * 이미 지원했거나 현재 날짜가 지원 시작 날짜 이전 혹은 지원 마감 날짜 이후일 경우 예외를 발생시킵니다.
     */
    fun validateCanApply(
        memberId: Long,
        activity: Activity,
    ) {
        val existingParticipation = participationRepository.findByMemberIdAndActivityId(memberId, activity.id)
        if (existingParticipation != null) {
            throw BusinessException(ErrorCode.ALREADY_APPLIED_ACTIVITY)
        }

        val now = LocalDate.now()
        if (now.isBefore(activity.recruitmentStartDate)) {
            throw BusinessException(ErrorCode.RECRUITMENT_NOT_STARTED)
        }
        if (now.isAfter(activity.recruitmentEndDate)) {
            throw BusinessException(ErrorCode.RECRUITMENT_ENDED)
        }
    }
}
