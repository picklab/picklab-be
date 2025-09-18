package picklab.backend.activity.entrypoint

import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import picklab.backend.activity.application.BookmarkUseCase
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.entrypoint.request.GetMyBookmarkListRequest
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.common.model.toPageResponse

@RestController
class ActivityBookmarkController(
    private val bookmarkUseCase: BookmarkUseCase,
) : ActivityBookmarkApi {
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

    @GetMapping("/v1/bookmarks")
    override fun getBookmarks(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @ModelAttribute request: GetMyBookmarkListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityItemWithBookmark>>> =
        bookmarkUseCase
            .getBookmarks(request.toCondition(member.memberId))
            .toPageResponse()
            .let { ResponseWrapper.success(SuccessCode.GET_BOOKMARKS, it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
}
