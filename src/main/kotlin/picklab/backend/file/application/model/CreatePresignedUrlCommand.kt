package picklab.backend.file.application.model

import picklab.backend.file.FileCategory

class CreatePresignedUrlCommand(
    val fileName: String,
    val category: FileCategory,
    val fileSize: Long,
    val memberId: Long,
)
