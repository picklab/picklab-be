package picklab.backend.member.entrypoint

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.member.application.MemberUseCase
import picklab.backend.member.entrypoint.request.AdditionalInfoRequest
import picklab.backend.member.entrypoint.request.MemberWithdrawalRequest
import picklab.backend.member.entrypoint.request.SendEmailRequest
import picklab.backend.member.entrypoint.request.ToggleMemberNotificationRequest
import picklab.backend.member.entrypoint.request.UpdateEmailAgreementRequest
import picklab.backend.member.entrypoint.request.UpdateEmailRequest
import picklab.backend.member.entrypoint.request.UpdateInfoRequest
import picklab.backend.member.entrypoint.request.UpdateJobCategoriesRequest
import picklab.backend.member.entrypoint.request.UpdateProfileImageRequest
import picklab.backend.member.entrypoint.request.VerifyEmailCodeRequest
import picklab.backend.member.entrypoint.response.GetSocialLoginsResponse

@RestController
@RequestMapping("/v1/members")
class MemberController(
    private val memberUseCase: MemberUseCase,
) : MemberApi {
    @PostMapping("/signup/additional-info")
    override fun additionalInfo(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: AdditionalInfoRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.updateAdditionalInfo(
            memberId = member.memberId,
            request = request,
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.SIGNUP_SUCCESS, Unit))
    }

    @PutMapping("/info")
    override fun updateMemberInfo(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: UpdateInfoRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.updateMemberInfo(
            memberId = member.memberId,
            request = request,
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.MEMBER_INFO_UPDATED, Unit))
    }

    @PutMapping("/job-categories")
    override fun updateJobCategories(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: UpdateJobCategoriesRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.updateJobCategories(
            member.memberId,
            request.interestedJobCategories,
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.MEMBER_JOB_CATEGORY_UPDATED, Unit))
    }

    @PutMapping("/profile-image")
    override fun updateProfileImage(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: UpdateProfileImageRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.updateProfileImage(
            member.memberId,
            request,
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.MEMBER_PROFILE_IMAGE_UPDATED, Unit))
    }

    @PostMapping("/email")
    override fun changeEmail(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody email: UpdateEmailRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.updateEmail(
            member.memberId,
            email.email,
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.MEMBER_EMAIL_UPDATED, Unit))
    }

    @PostMapping("/email/code/send")
    override fun sendEmailVerificationCode(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: SendEmailRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.sendEmailVerificationCode(
            member.memberId,
            request,
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.SEND_EMAIL_CODE, Unit))
    }

    @PostMapping("/email/code/verify")
    override fun verifyEmailCode(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: VerifyEmailCodeRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.verifyCode(member.memberId, request)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.VERIFY_EMAIL_CODE, Unit))
    }

    @PatchMapping("/email-agreement")
    override fun updateEmailAgreement(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: UpdateEmailAgreementRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.updateEmailAgreement(member.memberId, request)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.UPDATE_EMAIL_AGREEMENT, Unit))
    }

    @GetMapping("/social-logins")
    override fun getSocialLogins(
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<GetSocialLoginsResponse>> {
        memberUseCase.getSocialLogins(member.memberId)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                ResponseWrapper.success(
                    SuccessCode.GET_MEMBER_SOCIAL_LOGINS,
                    memberUseCase.getSocialLogins(member.memberId),
                ),
            )
    }

    @DeleteMapping("")
    override fun withdrawMember(
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.withdrawMember(member.memberId)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.MEMBER_WITHDRAW, Unit))
    }

    @PostMapping("/withdrawal-survey")
    override fun submitWithdrawalSurvey(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: MemberWithdrawalRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.submitWithdrawalSurvey(member.memberId, request)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.SUBMIT_SURVEY, Unit))
    }

    @PatchMapping("/notifications")
    override fun toggleNotification(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestBody request: ToggleMemberNotificationRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        memberUseCase.toggleMemberNotification(member.memberId, request)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.MEMBER_NOTIFICATION_UPDATED, Unit))
    }
}
