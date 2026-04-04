package picklab.backend.activitygroup.application.mapper

import picklab.backend.activitygroup.application.model.ActivityGroupCreateCommand
import picklab.backend.activitygroup.application.query.model.ActivityGroupView
import picklab.backend.activitygroup.domain.entity.ActivityGroup

fun ActivityGroupCreateCommand.toEntity(): ActivityGroup =
    ActivityGroup(
        name = name,
        description = description,
    )

fun ActivityGroup.toView(): ActivityGroupView =
    ActivityGroupView(
        id = id,
        name = name,
        description = description,
    )
