package picklab.backend.member.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.member.domain.entity.MemberActivityViewHistory

interface MemberActivityViewHistoryRepository : JpaRepository<MemberActivityViewHistory, Long>
