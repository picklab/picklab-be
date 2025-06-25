package picklab.backend.archive.entrypoint

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import picklab.backend.archive.application.ArchiveUseCase
import picklab.backend.archive.entrypoint.request.ArchiveCreateRequest
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode

@RestController
class ArchiveController(
    private val archiveUseCase: ArchiveUseCase,
) : ArchiveApi {

    @PostMapping("/v1/archive")
    override fun create(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestBody request: ArchiveCreateRequest
    ): ResponseEntity<ResponseWrapper<Unit>> {
        archiveUseCase.createArchive(request, member)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.CREATE_ARCHIVE_SUCCESS))
    }
}