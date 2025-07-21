package picklab.backend.participation.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper

@Tag(name = "활동 지원 API", description = "활동 지원 관련된 API")
interface ActivityParticipationApi {
    @Operation(
        summary = "활동 지원",
        description = "해당 유저가 특정 ID값의 활동에 지원합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "지원이 완료되었습니다."),
            ApiResponse(responseCode = "404", description = "해당 활동을 찾을 수 없습니다."),
            ApiResponse(responseCode = "409", description = "이미 지원한 활동입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun applyActivity(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>>
}
