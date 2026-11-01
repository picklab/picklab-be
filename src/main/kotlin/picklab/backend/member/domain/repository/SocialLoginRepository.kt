package picklab.backend.member.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import picklab.backend.member.domain.entity.SocialLogin
import picklab.backend.member.domain.enums.SocialType

interface SocialLoginRepository : JpaRepository<SocialLogin, Long> {
    fun findBySocialTypeAndSocialId(
        socialType: SocialType,
        socialId: String,
    ): SocialLogin?

    fun findAllByMemberId(memberId: Long): List<SocialLogin>

    @Modifying
    @Query("DELETE FROM SocialLogin sl WHERE sl.member.id = :memberId")
    fun deleteAllByMemberId(memberId: Long): Int
}
