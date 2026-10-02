package picklab.backend.file.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

data class CreatePresignedurlResponse(
    @field:Schema(description = "파일 업로드용 Presigned URL (PUT, 유효기간 10분)")
    val presignedUrl: String,
    @field:Schema(
        description = "업로드 대상의 임시 객체 키",
        example = "temp/profile/1/abcd1234_20261002_120000.jpg",
    )
    val objectKey: String,
)
