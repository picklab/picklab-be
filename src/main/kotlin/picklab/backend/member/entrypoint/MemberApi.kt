package picklab.backend.member.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.member.entrypoint.request.*
import picklab.backend.member.entrypoint.response.GetSocialLoginsResponse

@Tag(name = "회원 API", description = "회원 관련 작업을 하는 API")
interface MemberApi {
    @Operation(
        summary = "회원 추가 정보 기입",
        description = "회원의 추가 정보를 기입합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 추가 정보 기입에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "이미 사용 중인 닉네임입니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun additionalInfo(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: AdditionalInfoRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "사용자 정보 수정",
        description = "사용자의 정보를 수정한다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 정보 수정에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "이미 사용 중인 닉네임입니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun updateMemberInfo(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: UpdateInfoRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "사용자 관심직무 수정",
        description = "사용자의 관심직무 여부를 수정한다(최대 5개)",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 관심직무 수정에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "회원을 찾을 수 없습니다."),
            ApiResponse(responseCode = "400", description = "관심 직군은 최대 5개까지 선택할 수 있습니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun updateJobCategories(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: UpdateJobCategoriesRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "사용자 프로필 이미지 수정",
        description = "사용자의 프로필 이미지를 수정한다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 프로필 이미지 수정에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "회원을 찾을 수 없습니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun updateProfileImage(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: UpdateProfileImageRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "이메일 주소를 변경",
        description = "사용자의 이메일 주소를 변경할 수 있다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이메일 주소 변경에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "회원을 찾을 수 없습니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun changeEmail(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody email: UpdateEmailRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "이메일 인증 코드 전송",
        description = "이메일 주소 확인을 위한 인증코드를 발송할 수 있다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이메일 인증 코드 전송에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "회원을 찾을 수 없습니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun sendEmailVerificationCode(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: SendEmailRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "이메일 인증 코드 확인",
        description = "이메일 주소 인증번호를 검증할 수 있다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이메일 인증 코드 확인에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "인증 코드가 유효하지 않습니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun verifyEmailCode(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: VerifyEmailCodeRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "이메일 수신 동의 여부 수정",
        description = "이메일 마케팅 수신 동의 정보를 수정한다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이메일 수신 동의 여부 수정에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun updateEmailAgreement(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: UpdateEmailAgreementRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "소셜 로그인 정보 조회",
        description = "연동 소셜 로그인 정보를 조회한다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "소셜 로그인 정보 조회에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun getSocialLogins(
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<GetSocialLoginsResponse>>

    @Operation(
        summary = "회원 탈퇴",
        description = "회원 탈퇴 진행. soft delete 방식",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 탈퇴에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "회원을 찾을 수 없습니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun withdrawMember(
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "회원 탈퇴 설문 제출",
        description = "회원 탈퇴 설문을 제출한다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "탈퇴 설문 제출에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "회원을 찾을 수 없습니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun submitWithdrawalSurvey(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: MemberWithdrawalRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "알림 설정 토글",
        description = "사용자가 받을 알림을 토글한다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "알림 변경에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun toggleNotification(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestBody request: ToggleMemberNotificationRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>
}
