package picklab.backend.member.domain

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.member.domain.entity.MemberWithdrawal

interface MemberWithdrawalRepository : JpaRepository<MemberWithdrawal, Long>
