package picklab.backend.archive.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.archive.domain.enums.ArchiveSortType
import picklab.backend.archive.entrypoint.request.ArchiveCreateRequest
import picklab.backend.archive.entrypoint.request.ArchiveUpdateRequest
import picklab.backend.archive.entrypoint.response.ArchiveActivityResponse
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper

@Tag(name = "아카이브 API", description = "아카이브 관련 API 입니다.")
interface ArchiveApi {
    @Operation(
        summary = "아카이브 정보 생성",
        description = "아카이브 정보를 생성 합니다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "아카이브 생성에 성공했습니다."),
        ],
    )
    fun create(
        member: MemberPrincipal,
        request: ArchiveCreateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "아카이브 정보 수정",
        description = "아카이브 정보를 수정 합니다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "아카이브 수정에 성공했습니다."),
            ApiResponse(responseCode = "404", description = "아카이브 정보를 찾을 수 없습니다."),
        ],
    )
    fun update(
        @AuthenticationPrincipal member: MemberPrincipal,
        @PathVariable archiveId: Long,
        @RequestBody request: ArchiveUpdateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "아카이브 활동 목록 조회",
        description = "수료 완료한 활동 목록을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "아카이브 목록 조회에 성공했습니다."),
        ],
    )
    fun getList(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "활동 유형 필터 (미입력 시 전체)") @RequestParam(required = false) activityType: ActivityType?,
        @Parameter(description = "정렬 순서 (LATEST: 최신순, OLDEST: 오래된순)") @RequestParam(defaultValue = "LATEST") sort: ArchiveSortType,
    ): ResponseEntity<ResponseWrapper<List<ArchiveActivityResponse>>>
}
