package picklab.backend.member.application

import org.springframework.stereotype.Component
import picklab.backend.auth.domain.VerificationCodeService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.file.application.FileManagementService
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.job.domain.service.JobService
import picklab.backend.member.domain.MemberService
import picklab.backend.member.entrypoint.request.AdditionalInfoRequest
import picklab.backend.member.entrypoint.request.JobCategoryDto
import picklab.backend.member.entrypoint.request.MemberWithdrawalRequest
import picklab.backend.member.entrypoint.request.SendEmailRequest
import picklab.backend.member.entrypoint.request.ToggleMemberNotificationRequest
import picklab.backend.member.entrypoint.request.UpdateEmailAgreementRequest
import picklab.backend.member.entrypoint.request.UpdateInfoRequest
import picklab.backend.member.entrypoint.request.UpdateProfileImageRequest
import picklab.backend.member.entrypoint.request.VerifyEmailCodeRequest
import picklab.backend.member.infrastructure.MailService

@Component
class MemberUseCase(
    private val memberService: MemberService,
    private val jobService: JobService,
    private val mailService: MailService,
    private val verificationCodeService: VerificationCodeService,
    private val fileManagementService: FileManagementService,
) {
    fun updateAdditionalInfo(
        memberId: Long,
        request: AdditionalInfoRequest,
    ) {
        if (memberService.existByNickname(request.nickname)) {
            throw BusinessException(ErrorCode.EXISTS_NICKNAME)
        }

        val member = memberService.insertAdditionalInfo(memberId, request)

        val jobCategories = jobService.findJobCategoriesByGroupAndDetail(request.toJobGroupDetailMap())

        memberService.registerInterestedJobCategories(
            member = member,
            jobCategories = jobCategories,
        )
    }

    fun updateMemberInfo(
        memberId: Long,
        request: UpdateInfoRequest,
    ) {
        if (memberService.existByNickname(request.nickname)) {
            throw BusinessException(ErrorCode.EXISTS_NICKNAME)
        }

        memberService.updateMemberInfo(memberId, request)
    }

    fun updateJobCategories(
        memberId: Long,
        request: List<JobCategoryDto>,
    ) {
        if (request.size > 5) {
            throw BusinessException(ErrorCode.JOB_CATEGORY_LIMIT)
        }
        val member = memberService.clearInterestedJobCategories(memberId)

        val jobCategoryList = request.map { JobGroup.valueOf(it.group) to JobDetail.valueOf(it.detail) }

        val jobCategories = jobService.findJobCategoriesByGroupAndDetail(jobCategoryList)

        memberService.registerInterestedJobCategories(
            member = member,
            jobCategories = jobCategories,
        )
    }

    fun updateProfileImage(
        memberId: Long,
        request: UpdateProfileImageRequest,
    ) {
        val member = memberService.findActiveMember(memberId)

        if (member.profileImageUrl.isNotEmpty()) {
            fileManagementService.deleteFile(member.profileImageUrl)
        }

        val permanentImageUrl = fileManagementService.verifyTempFileAndMoveToPermanent(request.profileImage)

        memberService.updateProfileImage(member, permanentImageUrl)
    }

    fun updateEmail(
        memberId: Long,
        email: String,
    ) {
        memberService.updateEmail(memberId, email)
    }

    fun sendEmailVerificationCode(
        memberId: Long,
        request: SendEmailRequest,
    ) {
        val exist = memberService.findVerificationCode(memberId, request.email)
        if (exist != null) {
            memberService.invalidateVerificationCode(exist)
        }
        val code = verificationCodeService.createCode()
        memberService.addEmailVerificationCode(memberId, request.email, code)
        mailService.sendMail(request.email, code)
    }

    fun verifyCode(
        memberId: Long,
        request: VerifyEmailCodeRequest,
    ) {
        val verification = memberService.findVerification(memberId, request.code)

        memberService.invalidateVerificationCode(verification)
    }

    fun updateEmailAgreement(
        memberId: Long,
        request: UpdateEmailAgreementRequest,
    ) {
        memberService.updateEmailAgreement(memberId, request.emailAgreement)
    }

    fun getSocialLogins(memberId: Long) = memberService.getSocialLogins(memberId)

    fun withdrawMember(memberId: Long) {
        memberService.withdrawMember(memberId)
    }

    fun submitWithdrawalSurvey(
        memberId: Long,
        request: MemberWithdrawalRequest,
    ) {
        memberService.submitWithdrawalSurvey(memberId, request)
    }

    fun toggleMemberNotification(
        memberId: Long,
        request: ToggleMemberNotificationRequest,
    ) {
        memberService.toggleMemberNotification(memberId, request.type)
    }
}
