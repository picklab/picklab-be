package picklab.backend.common.file.application.model

import picklab.backend.common.file.FileCategory

class CreatePresignedUrlCommand(
    val fileName: String,
    val category: FileCategory,
    val memberId: Long,
)
