package picklab.backend.member.domain

import java.time.LocalDateTime

data class MemberWithdrawnEvent(
    val memberId: Long,
    val withdrawnAt: LocalDateTime,
)
