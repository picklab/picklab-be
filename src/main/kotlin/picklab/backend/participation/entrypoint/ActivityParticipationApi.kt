package picklab.backend.participation.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.entrypoint.request.UpdateApplicationStatusRequest
import picklab.backend.participation.entrypoint.request.UpdateProgressStatusRequest
import picklab.backend.participation.entrypoint.response.ActivityParticipationResultResponse
import picklab.backend.participation.entrypoint.response.ActivityParticipationSummaryResponse

@Tag(name = "활동 지원 API", description = "활동 지원 관련된 API")
interface ActivityParticipationApi {
    @Operation(
        summary = "활동 지원 완료 표시",
        description = "로그인한 사용자가 활동을 지원 완료로 표시합니다.",
        responses = [ApiResponse(responseCode = "201", description = "활동 지원 완료 표시 성공")],
    )
    fun create(
        member: MemberPrincipal,
        activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "활동 지원 완료 표시 취소",
        description = "로그인한 사용자의 활동 지원 완료 표시를 취소합니다.",
        responses = [ApiResponse(responseCode = "200", description = "활동 지원 완료 표시 취소 성공")],
    )
    fun cancel(
        member: MemberPrincipal,
        activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "합격 여부 수정",
        description = "활동 참여의 지원 상태를 수정합니다.",
    )
    fun updateApplicationStatus(
        member: MemberPrincipal,
        participationId: Long,
        @Valid request: UpdateApplicationStatusRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "수료 여부 수정",
        description = "최종 합격한 활동 참여의 수료 상태를 수정합니다.",
    )
    fun updateProgressStatus(
        member: MemberPrincipal,
        participationId: Long,
        @Valid request: UpdateProgressStatusRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "활동 결과 목록 조회",
        description = "로그인한 사용자가 지원 완료로 표시한 활동 결과 목록을 조회합니다.",
    )
    fun getResults(
        member: MemberPrincipal,
        applicationStatus: List<ApplicationStatus>?,
        @Min(1) page: Int,
        @Min(1) @Max(100) size: Int,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityParticipationResultResponse>>>

    @Operation(
        summary = "활동 결과 현황 조회",
        description = "지원 완료, 최종 합격, 불합격, 수료 완료 개수를 조회합니다.",
    )
    fun getSummary(member: MemberPrincipal): ResponseEntity<ResponseWrapper<ActivityParticipationSummaryResponse>>
}
