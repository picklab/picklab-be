package picklab.backend.activity.domain.service

import org.springframework.stereotype.Service
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
) {

    fun mustFindById(activityId: Long): Activity = activityRepository
        .findById(activityId)
        .orElseThrow { throw BusinessException(ErrorCode.NOT_FOUND_ACTIVITY) }
}