package picklab.backend.auth.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import picklab.backend.member.domain.enums.SocialType

@Tag(name = "소셜 로그인 API", description = "소셜 로그인을 관리하는 API")
interface AuthApi {
    @Operation(
        summary = "소셜 로그인 요청",
        description = "클라이언트를 소셜 로그인 제공자의 인증 페이지로 리다이렉트합니다.",
        responses = [
            ApiResponse(
                responseCode = "302",
                description = "소셜 로그인 제공자의 인증 페이지로 리다이렉트합니다.",
            ),
        ],
    )
    fun login(
        @Parameter(description = "소셜 로그인 제공자", required = true) provider: SocialType,
    ): ResponseEntity<Unit>

    @Operation(
        summary = "소셜 로그인 콜백 처리",
        description = """
            소셜 로그인 제공자로부터 전달받은 인증 코드를 바탕으로 사용자 정보를 조회하고 로그인 처리를 수행합니다.  
            처리 완료 후 쿠키에 인증 토큰을 담아 프론트엔드로 리다이렉트합니다.
            """,
        responses = [
            ApiResponse(
                responseCode = "302",
                description = "로그인 성공. 프론트엔드로 리다이렉트 (쿠키에 토큰 포함)",
            ),
            ApiResponse(
                responseCode = "400",
                description = "소셜 로그인 유저 정보가 올바르지 않습니다.",
            ),
            ApiResponse(
                responseCode = "401",
                description = "소셜 로그인 인증 코드가 유효하지 않습니다.",
            ),
            ApiResponse(
                responseCode = "502",
                description = "소셜 로그인 유저 정보 조회에 실패하였습니다.",
            ),
        ],
    )
    fun handleCallback(
        @Parameter(description = "소셜 로그인 제공자", required = true) provider: SocialType,
        @Parameter(description = "인증 코드", required = true) code: String,
    ): ResponseEntity<Unit>
}
