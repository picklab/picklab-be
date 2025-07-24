package picklab.backend.participation.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.MemberService
import picklab.backend.participation.domain.service.ActivityParticipationService
import picklab.backend.participation.entrypoint.response.GetActivityApplicationUrlResponse

@Component
class ActivityParticipationUseCase(
    private val memberService: MemberService,
    private val activityService: ActivityService,
    private val activityParticipationService: ActivityParticipationService,
) {
    /**
     * 유효한 활동인지 확인하고, 해당 활동에 지원할 수 있는 링크를 반환합니다.
     */
    @Transactional
    fun getActivityApplicationUrl(activityId: Long): GetActivityApplicationUrlResponse {
        val activity = activityService.mustFindById(activityId)

        val applicationUrl = activity.activityApplicationUrl

        if (applicationUrl.isNullOrBlank()) {
            throw BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_APPLICATION_URL)
        }

        return GetActivityApplicationUrlResponse(applicationUrl)
    }
}
