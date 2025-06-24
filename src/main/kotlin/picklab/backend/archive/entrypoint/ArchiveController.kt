package picklab.backend.archive.entrypoint

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import picklab.backend.archive.application.ArchiveUseCase
import picklab.backend.archive.entrypoint.request.ArchiveCreateRequest
import picklab.backend.common.model.MemberPrincipal

@RestController
class ArchiveController (
    private val archiveUseCase: ArchiveUseCase,
){

    @PostMapping("/v1/archive")
    fun create(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestBody request: ArchiveCreateRequest
    ) {
        archiveUseCase.createArchive(request, member)
    }
}