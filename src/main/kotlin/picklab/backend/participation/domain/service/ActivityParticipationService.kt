package picklab.backend.participation.domain.service

import org.springframework.stereotype.Service
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.participation.domain.entity.ActivityParticipation
import picklab.backend.participation.domain.repository.ActivityParticipationRepository

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
}
