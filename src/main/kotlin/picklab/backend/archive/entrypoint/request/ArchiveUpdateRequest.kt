package picklab.backend.archive.entrypoint.request

import picklab.backend.archive.domain.enums.PassOrFailStatus
import picklab.backend.archive.domain.enums.ProgressStatus

class ArchiveUpdateRequest(
    val activityProgressStatus: ProgressStatus,
    val passOrFailStatus: PassOrFailStatus,
)