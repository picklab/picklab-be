package picklab.backend.bookmark.entrypoint

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import picklab.backend.bookmark.application.BookmarkUseCase
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode

@RestController
class BookmarkController(
    private val bookmarkUseCase: BookmarkUseCase,
) : BookmarkApi {
    @PostMapping("/v1/activities/{activityId}/bookmarks")
    override fun createActivityBookmark(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>> =
        bookmarkUseCase
            .createActivityBookmark(
                memberId = member.memberId,
                activityId = activityId,
            ).let { ResponseWrapper.success(SuccessCode.ACTIVITY_BOOKMARK_CREATED) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @DeleteMapping("/v1/activities/{activityId}/bookmarks")
    override fun removeActivityBookmark(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>> =
        bookmarkUseCase
            .removeActivityBookmark(
                memberId = member.memberId,
                activityId = activityId,
            ).let { ResponseWrapper.success(SuccessCode.ACTIVITY_BOOKMARK_REMOVED) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
}
