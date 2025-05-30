package picklab.backend.auth

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper

@RestController
@RequestMapping("/v1/test")
class TestAuthController {
    @GetMapping("/auth/filter")
    fun testJwtFilter(
        @AuthenticationPrincipal principal: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<Long>> =
        ResponseEntity.ok().body(
            ResponseWrapper(
                code = 200,
                message = "성공",
                data = principal.memberId,
            ),
        )
}
