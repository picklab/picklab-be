package picklab.backend.member.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.member.domain.repository.MemberActivityViewHistoryRepository
import java.time.LocalDateTime

@Service
@Transactional
class MemberActivityViewHistoryService(
    private val memberActivityViewHistoryRepository: MemberActivityViewHistoryRepository,
) {
    fun recordActivityView(
        memberId: Long,
        activityId: Long,
        targetDate: LocalDateTime,
    ) {
        memberActivityViewHistoryRepository.upsertViewHistory(memberId, activityId, targetDate)
    }
}
