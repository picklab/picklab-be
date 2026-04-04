package picklab.backend.activitygroup.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activitygroup.domain.entity.ActivityGroup
import picklab.backend.activitygroup.domain.repository.ActivityGroupRepository
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode

@Service
class ActivityGroupService(
    private val activityGroupRepository: ActivityGroupRepository,
) {
    @Transactional
    fun save(entity: ActivityGroup): ActivityGroup = activityGroupRepository.save(entity)

    @Transactional(readOnly = true)
    fun mustFindById(id: Long): ActivityGroup =
        activityGroupRepository
            .findById(id)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_GROUP) }
}
