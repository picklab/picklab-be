package picklab.backend.member.domain

import org.springframework.stereotype.Service
import picklab.backend.auth.domain.OAuthUserInfo
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.enums.SocialType

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val socialLoginRepository: SocialLoginRepository,
) {
    fun loginOrSignup(
        socialType: String,
        userInfo: OAuthUserInfo,
    ): Member {
        val socialType = SocialType.from(socialType)
        val socialLogin = socialLoginRepository.findBySocialTypeAndSocialId(socialType, userInfo.getSocialId())

        return socialLogin?.member ?: run {
            val newMember =
                Member(
                    name = userInfo.getName() ?: throw IllegalArgumentException("Name is required"),
                    email = userInfo.getEmail() ?: throw IllegalArgumentException("Email is required"),
                    profileImageUrl = userInfo.getProfileImage() ?: throw IllegalArgumentException("Profile image URL is required"),
                    birthDate = userInfo.getBirthdate(),
                )

            newMember.addSocialLogin(
                socialType = socialType,
                socialId = userInfo.getSocialId(),
            )

            memberRepository.save(newMember)
        }
    }

    fun saveRefreshToken(
        memberId: Long,
        refreshToken: String,
    ) {
        val member =
            memberRepository
                .findById(memberId)
                .orElseThrow { IllegalArgumentException("Member not found") }

        member.updateRefreshToken(refreshToken)
        memberRepository.save(member)
    }
}
