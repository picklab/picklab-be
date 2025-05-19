package picklab.backend.member.domain

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.member.domain.entity.Member

interface MemberRepository : JpaRepository<Member, Long>
