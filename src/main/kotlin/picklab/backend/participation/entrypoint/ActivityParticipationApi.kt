package picklab.backend.participation.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.participation.entrypoint.response.GetActivityApplicationUrlResponse

@Tag(name = "활동 지원 API", description = "활동 지원 관련된 API")
interface ActivityParticipationApi {
    @Operation(
        summary = "활동 지원",
        description = "해당 활동에 지원할 수 있는 링크를 반환합니다. 링크가 존재하지 않을 경우 404 에러 및 대체 문구를 반환합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "활동 지원 링크를 가져오는데 성공했습니다."),
            ApiResponse(responseCode = "404", description = "별도 지원 방식으로 진행됩니다. 아래 상세 내용을 참고해 주세요."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun getActivityApplicationUrl(
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<GetActivityApplicationUrlResponse>>
}
