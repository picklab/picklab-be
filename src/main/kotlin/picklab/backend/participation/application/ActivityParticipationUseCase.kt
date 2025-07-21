package picklab.backend.participation.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.member.domain.MemberService
import picklab.backend.participation.domain.service.ActivityParticipationService

@Component
class ActivityParticipationUseCase(
    private val memberService: MemberService,
    private val activityService: ActivityService,
    private val activityParticipationService: ActivityParticipationService,
) {
    /**
     * 지원가능한 활동인지 검사하고, 지원 가능할 시 활동에 지원합니다.
     */
    @Transactional
    fun applyToActivity(
        memberId: Long,
        activityId: Long,
    ) {
        val member = memberService.findActiveMember(memberId)
        val activity = activityService.mustFindById(activityId)

        activityParticipationService.validateCanApply(member.id, activity)
        activityParticipationService.applyToActivity(member, activity)
    }
}
