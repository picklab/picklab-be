package picklab.backend.activitygroup.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activitygroup.application.mapper.toEntity
import picklab.backend.activitygroup.application.model.ActivityGroupCreateCommand
import picklab.backend.activitygroup.domain.service.ActivityGroupService

@Component
class ActivityGroupUseCase(
    private val activityGroupService: ActivityGroupService,
) {
    @Transactional
    fun createActivityGroup(command: ActivityGroupCreateCommand) {
        activityGroupService.save(command.toEntity())
    }
}
