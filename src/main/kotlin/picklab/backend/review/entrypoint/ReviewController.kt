package picklab.backend.review.entrypoint

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import picklab.backend.archive.entrypoint.request.ReviewCreateRequest
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.common.model.toPageResponse
import picklab.backend.review.application.ReviewUseCase
import picklab.backend.review.application.query.model.MyReviewListView
import picklab.backend.review.entrypoint.mapper.toResponse
import picklab.backend.review.entrypoint.request.ActivityReviewListRequest
import picklab.backend.review.entrypoint.request.MyReviewListRequest
import picklab.backend.review.entrypoint.request.ReviewUpdateRequest
import picklab.backend.review.entrypoint.response.ActivityReviewResponse
import picklab.backend.review.entrypoint.response.MyReviewResponse
import picklab.backend.review.entrypoint.response.MyReviewsResponse

@RestController
class ReviewController(
    private val reviewUseCase: ReviewUseCase,
) : ReviewApi {
    @PostMapping("/v1/review")
    override fun create(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: ReviewCreateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> =
        reviewUseCase
            .createReview(request.toCommand(member.memberId))
            .let { ResponseEntity.ok(ResponseWrapper.success(SuccessCode.CREATE_REVIEW_SUCCESS)) }

    @GetMapping("/v1/reviews/{id}")
    override fun getMyReview(
        @PathVariable id: Long,
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<MyReviewResponse>> =
        reviewUseCase
            .getMyReview(id, member.memberId)
            .toResponse()
            .let { ResponseWrapper.success(SuccessCode.GET_REVIEW, it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/v1/reviews")
    override fun getMyReviews(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @ModelAttribute request: MyReviewListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<MyReviewsResponse>>> =
        reviewUseCase
            .getMyReviews(request.toQueryRequest(member.memberId))
            .toPageResponse(MyReviewListView::toResponse)
            .let { ResponseWrapper.success(SuccessCode.GET_REVIEWS, it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/v1/activities/{activityId}/reviews")
    override fun getReviewsByActivity(
        @PathVariable activityId: Long,
        @AuthenticationPrincipal member: MemberPrincipal?,
        @Valid @ModelAttribute request: ActivityReviewListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityReviewResponse>>> =
        reviewUseCase
            .getReviewsByActivity(
                request.toQueryRequest(),
                activityId,
                member?.memberId,
                request.page,
                request.size,
            ).toPageResponse { it.toResponse(member) }
            .let { ResponseWrapper.success(SuccessCode.GET_REVIEWS, it) }
            .let { ResponseEntity.ok(it) }

    @PutMapping("/v1/reviews/{id}")
    override fun updateReview(
        @PathVariable id: Long,
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: ReviewUpdateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        reviewUseCase.updateReview(request.toCommand(id, member.memberId))
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.UPDATE_REVIEW_SUCCESS))
    }

    @DeleteMapping("/v1/reviews/{id}")
    override fun deleteReview(
        @PathVariable id: Long,
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        reviewUseCase.deleteReview(member.memberId, id)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.DELETE_REVIEW_SUCCESS))
    }
}
