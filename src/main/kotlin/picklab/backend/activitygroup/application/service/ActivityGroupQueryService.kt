package picklab.backend.activitygroup.application.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activitygroup.application.mapper.toView
import picklab.backend.activitygroup.application.query.model.ActivityGroupView
import picklab.backend.activitygroup.domain.repository.ActivityGroupRepository

@Service
class ActivityGroupQueryService(
    private val activityGroupRepository: ActivityGroupRepository,
) {
    @Transactional(readOnly = true)
    fun findAll(): List<ActivityGroupView> =
        activityGroupRepository
            .findAll(Sort.by(Sort.Direction.ASC, "id"))
            .map { it.toView() }
}
