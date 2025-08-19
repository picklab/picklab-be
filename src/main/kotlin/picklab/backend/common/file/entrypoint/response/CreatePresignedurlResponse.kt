package picklab.backend.common.file.entrypoint.response

data class CreatePresignedurlResponse(
    val presignedUrl: String,
    val publicReadUrl: String,
)
