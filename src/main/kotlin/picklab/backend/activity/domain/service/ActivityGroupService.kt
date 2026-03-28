package picklab.backend.activity.domain.service

import org.springframework.stereotype.Service
import picklab.backend.activity.domain.entity.ActivityGroup
import picklab.backend.activity.domain.repository.ActivityGroupRepository
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode

@Service
class ActivityGroupService(
    private val activityGroupRepository: ActivityGroupRepository,
) {
    fun mustFindById(id: Long): ActivityGroup =
        activityGroupRepository
            .findById(id)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_GROUP) }
}
