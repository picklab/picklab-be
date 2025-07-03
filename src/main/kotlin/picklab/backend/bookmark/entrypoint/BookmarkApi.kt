package picklab.backend.bookmark.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper

interface BookmarkApi {
    @Operation(
        summary = "활동 북마크 생성",
        description = "해당 유저가 특정 활동 ID값에 대한 북마크를 생성합니다.",
        responses = [
            ApiResponse(responseCode = "201", description = "북마크가 추가되었습니다."),
            ApiResponse(responseCode = "400", description = "이미 북마크된 활동입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun createActivityBookmark(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "활동 북마크 해제",
        description = "해당 유저가 특정 활동 ID값에 대한 북마크를 해제합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "북마크가 해제되었습니다."),
            ApiResponse(responseCode = "404", description = "북마크된 활동을 찾을 수 없습니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun removeActivityBookmark(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>>
}
