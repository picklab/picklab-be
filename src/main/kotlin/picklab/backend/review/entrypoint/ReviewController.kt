package picklab.backend.review.entrypoint

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import picklab.backend.archive.entrypoint.request.ReviewCreateRequest
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.review.application.ReviewUseCase

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
}
