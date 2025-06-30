package picklab.backend.activity.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.model.ActivityItem
import picklab.backend.activity.application.model.ActivitySearchCommand

interface ActivityRepositoryCustom {
    fun getActivities(
        queryData: ActivitySearchCommand,
        pageable: PageRequest,
    ): Page<ActivityItem>
}
