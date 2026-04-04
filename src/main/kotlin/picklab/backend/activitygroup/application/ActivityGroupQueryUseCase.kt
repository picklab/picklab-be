package picklab.backend.activitygroup.application

import org.springframework.stereotype.Component
import picklab.backend.activitygroup.application.query.model.ActivityGroupView
import picklab.backend.activitygroup.application.service.ActivityGroupQueryService

@Component
class ActivityGroupQueryUseCase(
    private val activityGroupQueryService: ActivityGroupQueryService,
) {
    fun getActivityGroups(): List<ActivityGroupView> = activityGroupQueryService.findAll()
}
