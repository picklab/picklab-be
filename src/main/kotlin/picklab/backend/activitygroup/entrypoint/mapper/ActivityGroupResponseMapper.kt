package picklab.backend.activitygroup.entrypoint.mapper

import picklab.backend.activitygroup.application.query.model.ActivityGroupView
import picklab.backend.activitygroup.entrypoint.response.ActivityGroupResponse

fun ActivityGroupView.toResponse(): ActivityGroupResponse =
    ActivityGroupResponse(
        id = id,
        name = name,
        description = description,
    )
