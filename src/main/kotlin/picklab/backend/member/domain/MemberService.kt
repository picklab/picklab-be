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
        socialType: SocialType,
        userInfo: OAuthUserInfo,
    ): Member {
        val socialLogin = socialLoginRepository.findBySocialTypeAndSocialId(socialType, userInfo.getSocialId())

        return socialLogin?.member ?: run {
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
