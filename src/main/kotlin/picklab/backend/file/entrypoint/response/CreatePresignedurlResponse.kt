package picklab.backend.file.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

data class CreatePresignedurlResponse(
    @field:Schema(description = "파일 본문을 직접 PUT하는 업로드 URL. 발급 후 10분간 유효하며 조회용 URL이 아닙니다.")
    val presignedUrl: String,
    @field:Schema(
        description = "업로드할 임시 객체 키. PUT 성공 후 리뷰 등록·수정 요청의 objectKey로 전달합니다.",
        example = "temp/review/1/10/abcd1234_20261002_120000.jpg",
    )
    val objectKey: String,
)
