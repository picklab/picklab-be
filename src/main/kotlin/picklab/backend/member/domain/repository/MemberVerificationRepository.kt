package picklab.backend.member.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import picklab.backend.member.domain.entity.MemberVerification

interface MemberVerificationRepository : JpaRepository<MemberVerification, Long> {
    fun findByMemberIdAndEmailAndDeletedAtIsNull(
        memberId: Long,
        email: String,
    ): MemberVerification?

    @Query(
        """
        SELECT mv 
        FROM MemberVerification mv 
        WHERE mv.member.id = :memberId 
        AND mv.code = :code 
        AND mv.expiredAt > CURRENT_TIMESTAMP 
        AND mv.deletedAt IS NULL
        """,
    )
    fun findVerificationCode(
        memberId: Long,
        code: String,
    ): MemberVerification?
}
