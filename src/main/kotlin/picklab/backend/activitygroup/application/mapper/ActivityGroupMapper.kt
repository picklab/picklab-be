package picklab.backend.activitygroup.application.mapper

import picklab.backend.activitygroup.application.model.ActivityGroupCreateCommand
import picklab.backend.activitygroup.domain.entity.ActivityGroup

fun ActivityGroupCreateCommand.toEntity(): ActivityGroup =
    ActivityGroup(
        name = name,
        description = description,
    )
