package picklab.backend.archive.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import picklab.backend.archive.entrypoint.request.ArchiveCreateRequest
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode

@Tag(name = "아카이브 API", description = "아카이브 관련 API 문서입니다.")
interface ArchiveApi {

    @Operation(
        summary = "회원 추가 정보 기입",
        description = "회원의 추가 정보를 기입합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "아카이브 생성에 성공했습니다."),
        ],
    )
    fun create(member: MemberPrincipal, request: ArchiveCreateRequest): ResponseEntity<ResponseWrapper<Unit>>
}