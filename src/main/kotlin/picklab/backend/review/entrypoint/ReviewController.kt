package picklab.backend.review.entrypoint

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
import picklab.backend.review.application.ReviewUseCase
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
    ): ResponseEntity<ResponseWrapper<Unit>> {
        reviewUseCase.createReview(request.toCommand(member.memberId))
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.CREATE_REVIEW_SUCCESS))
    }

    @GetMapping("/v1/reviews/{id}")
    override fun getMyReview(
        @PathVariable id: Long,
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<MyReviewResponse>> {
        val res = reviewUseCase.getMyReview(id, member.memberId)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.GET_REVIEW, res))
    }

    @GetMapping("/v1/reviews")
    override fun getMyReviews(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @ModelAttribute request: MyReviewListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<MyReviewsResponse>>> {
        val res = reviewUseCase.getMyReviews(request.toQueryRequest(member.memberId))
        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.success(SuccessCode.GET_REVIEWS, res))
    }

    @GetMapping("/v1/activities/{activityId}/reviews")
    override fun getReviewsByActivity(
        @PathVariable activityId: Long,
        @AuthenticationPrincipal member: MemberPrincipal?,
        @Valid @ModelAttribute request: ActivityReviewListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityReviewResponse>>> {
        val res =
            reviewUseCase.getReviewsByActivity(
                request.toQueryRequest(),
                activityId,
                member?.memberId,
                request.page,
                request.size,
            )
        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.success(SuccessCode.GET_REVIEWS, res))
    }

    @PutMapping("/v1/reviews/{id}")
    override fun updateReview(
        @PathVariable id: Long,
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: ReviewUpdateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        reviewUseCase.updateReview(request.toCommand(id, member.memberId))
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.UPDATE_REVIEW_SUCCESS))
    }
}
