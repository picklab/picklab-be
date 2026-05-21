package picklab.backend.archive.entrypoint

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.archive.application.ArchiveUseCase
import picklab.backend.archive.domain.enums.ArchiveSortType
import picklab.backend.archive.entrypoint.request.ArchiveCreateRequest
import picklab.backend.archive.entrypoint.request.ArchiveUpdateRequest
import picklab.backend.archive.entrypoint.response.ArchiveActivityResponse
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
        @RequestBody request: ArchiveCreateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        archiveUseCase.createArchive(request, member)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.CREATE_ARCHIVE_SUCCESS))
    }

    @PatchMapping("/v1/archive/{archiveId}")
    override fun update(
        @AuthenticationPrincipal member: MemberPrincipal,
        @PathVariable archiveId: Long,
        @RequestBody request: ArchiveUpdateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        archiveUseCase.updateArchive(archiveId, request, member)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.UPDATE_ARCHIVE_SUCCESS))
    }

    @GetMapping("/v1/archive")
    override fun getList(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestParam(required = false) activityType: ActivityType?,
        @RequestParam(defaultValue = "LATEST") sort: ArchiveSortType,
    ): ResponseEntity<ResponseWrapper<List<ArchiveActivityResponse>>> {
        val result = archiveUseCase.getArchiveList(activityType, sort, member)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ARCHIVE_LIST, result))
    }
}
