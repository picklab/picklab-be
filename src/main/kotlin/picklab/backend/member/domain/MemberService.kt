package picklab.backend.member.domain

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.auth.domain.OAuthUserInfo
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.member.domain.entity.InterestedJobCategory
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.entity.MemberVerification
import picklab.backend.member.domain.entity.MemberWithdrawal
import picklab.backend.member.domain.entity.NotificationPreference
import picklab.backend.member.domain.enums.NotificationType
import picklab.backend.member.domain.enums.SocialType
import picklab.backend.member.domain.repository.InterestedJobCategoryRepository
import picklab.backend.member.domain.repository.MemberAgreementRepository
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.member.domain.repository.MemberVerificationRepository
import picklab.backend.member.domain.repository.MemberWithdrawalRepository
import picklab.backend.member.domain.repository.SocialLoginRepository
import picklab.backend.member.domain.service.NotificationPreferenceService
import picklab.backend.member.entrypoint.request.AdditionalInfoRequest
import picklab.backend.member.entrypoint.request.MemberWithdrawalRequest
import picklab.backend.member.entrypoint.request.UpdateInfoRequest
import picklab.backend.member.entrypoint.response.GetSocialLoginsResponse
import java.time.LocalDateTime

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val socialLoginRepository: SocialLoginRepository,
    private val interestedJobCategoryRepository: InterestedJobCategoryRepository,
    private val memberVerificationRepository: MemberVerificationRepository,
    private val memberAgreementRepository: MemberAgreementRepository,
    private val memberWithdrawalRepository: MemberWithdrawalRepository,
    private val notificationPreferenceService: NotificationPreferenceService,
) {
    @Transactional
    fun loginOrSignup(
        socialType: SocialType,
        userInfo: OAuthUserInfo,
    ): Member {
        val socialLogin = socialLoginRepository.findBySocialTypeAndSocialId(socialType, userInfo.getSocialId())

        if (socialLogin == null) {
            val newMember =
                Member(
                    name = userInfo.getName(),
                    email = userInfo.getEmail(),
                    profileImageUrl = userInfo.getProfileImage(),
                    birthDate = userInfo.getBirthdate(),
                )

            newMember.addSocialLogin(
                socialType = socialType,
                socialId = userInfo.getSocialId(),
            )

            memberRepository.save(newMember)

            val notificationPreference =
                NotificationPreference(
                    member = newMember,
                    notifyPopularActivity = true,
                    notifyBookmarkedActivity = true,
                )

            notificationPreferenceService.save(notificationPreference)

            return newMember
        }

        return socialLogin.member
    }

    @Transactional
    fun saveRefreshToken(
        memberId: Long,
        refreshToken: String,
    ) {
        val member = findActiveMember(memberId)

        member.updateRefreshToken(refreshToken)
        memberRepository.save(member)
    }

    @Transactional
    fun insertAdditionalInfo(
        memberId: Long,
        request: AdditionalInfoRequest,
    ): Member {
        val member = findActiveMember(memberId)

        member.insertAdditionalInfo(
            nickname = request.nickname,
            educationLevel = request.educationLevel,
            school = request.school,
            graduationStatus = request.graduationStatus,
            employmentStatus = request.employmentStatus,
            company = request.company,
            employmentType = request.employmentType,
        )

        return memberRepository.save(member)
    }

    fun findMyInterestedJobCategoryIds(member: Member): List<Long> = interestedJobCategoryRepository.findJobCategoryIdsByMemberId(member.id)

    @Transactional
    fun registerInterestedJobCategories(
        member: Member,
        jobCategories: List<JobCategory>,
    ) {
        interestedJobCategoryRepository.saveAll(
            jobCategories.map { InterestedJobCategory(member, it) },
        )
    }

    @Transactional
    fun updateMemberInfo(
        memberId: Long,
        request: UpdateInfoRequest,
    ) {
        val member = findActiveMember(memberId)

        member.updateInfo(
            name = request.name,
            nickname = request.nickname,
            educationLevel = request.educationLevel,
            school = request.school,
            graduationStatus = request.graduationStatus,
            employmentStatus = request.employmentStatus,
            company = request.company,
            employmentType = request.employmentType,
        )

        memberRepository.save(member)
    }

    @Transactional
    fun clearInterestedJobCategories(memberId: Long): Member {
        val member = findActiveMember(memberId)

        interestedJobCategoryRepository.deleteAllByMember(member)

        return member
    }

    @Transactional
    fun updateProfileImage(
        member: Member,
        imageUrl: String,
    ) {
        member.updateProfileImage(imageUrl)
        memberRepository.save(member)
    }

    @Transactional
    fun updateEmail(
        memberId: Long,
        email: String,
    ) {
        val member = findActiveMember(memberId)

        member.updateEmail(email)
        memberRepository.save(member)
    }

    @Transactional(readOnly = true)
    fun findVerificationCode(
        memberId: Long,
        email: String,
    ): MemberVerification? =
        memberVerificationRepository.findByMemberIdAndEmailAndDeletedAtIsNull(
            memberId = memberId,
            email = email,
        )

    @Transactional
    fun addEmailVerificationCode(
        memberId: Long,
        email: String,
        code: String,
    ) {
        val member = findActiveMember(memberId)

        val now = LocalDateTime.now()

        memberVerificationRepository.save(
            MemberVerification(
                email = email,
                code = code,
                expiredAt = now.plusMinutes(5),
                member = member,
            ),
        )
    }

    @Transactional
    fun invalidateVerificationCode(exist: MemberVerification) {
        exist.deletedAt = LocalDateTime.now()
        memberVerificationRepository.save(exist)
    }

    @Transactional(readOnly = true)
    fun findVerification(
        memberId: Long,
        code: String,
    ): MemberVerification =
        memberVerificationRepository
            .findVerificationCode(memberId, code)
            ?: throw BusinessException(ErrorCode.INVALID_VERIFICATION_CODE)

    @Transactional
    fun updateEmailAgreement(
        memberId: Long,
        agree: Boolean,
    ) {
        val agreement =
            memberAgreementRepository.findByMemberId(memberId)
                ?: throw BusinessException(ErrorCode.BAD_REQUEST)

        agreement.updateEmailAgreement(agree)
        memberAgreementRepository.save(agreement)
    }

    @Transactional(readOnly = true)
    fun getSocialLogins(memberId: Long): GetSocialLoginsResponse {
        val socialLogins = socialLoginRepository.findAllByMemberId(memberId)

        return GetSocialLoginsResponse(
            socialLogins.map {
                it.socialType.name
            },
        )
    }

    @Transactional
    fun withdrawMember(memberId: Long) {
        val member = findActiveMember(memberId)

        member.withdraw()
        memberRepository.save(member)
    }

    @Transactional
    fun submitWithdrawalSurvey(
        memberId: Long,
        request: MemberWithdrawalRequest,
    ) {
        existActiveMember(memberId)

        memberWithdrawalRepository.save(
            MemberWithdrawal(
                memberId = memberId,
                withdrawalReason = request.reason,
                withdrawalReasonDetail = request.detail,
            ),
        )
    }

    @Transactional
    fun toggleMemberNotification(
        memberId: Long,
        type: NotificationType,
    ) {
        existActiveMember(memberId)
        notificationPreferenceService.toggleNotification(memberId, type)
    }

    fun findActiveMember(memberId: Long): Member =
        memberRepository
            .findByIdAndDeletedAtIsNull(memberId)
            .orElseThrow { BusinessException(ErrorCode.INVALID_MEMBER) }

    private fun existActiveMember(memberId: Long) {
        if (!memberRepository.existsByIdAndDeletedAtIsNull(memberId)) {
            throw BusinessException(ErrorCode.INVALID_MEMBER)
        }
    }

    fun existByNickname(nickname: String): Boolean = memberRepository.existsByNicknameAndDeletedAtIsNull(nickname)
}
