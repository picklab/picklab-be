package picklab.backend.member.domain

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.member.domain.entity.MemberAgreement

interface MemberAgreementRepository : JpaRepository<MemberAgreement, Long> {
    fun findByMemberId(memberId: Long): MemberAgreement?
}
