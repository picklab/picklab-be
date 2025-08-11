package picklab.backend.member.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.entity.MemberActivityViewHistory
import picklab.backend.member.domain.repository.MemberActivityViewHistoryRepository

@Service
@Transactional
class MemberActivityViewHistoryService(
    private val memberActivityViewHistoryRepository: MemberActivityViewHistoryRepository,
    private val memberService: MemberService,
) {
    fun recordActivityView(
        memberId: Long,
        activity: Activity,
    ) {
        val member = memberService.findActiveMember(memberId)

        val viewHistory =
            MemberActivityViewHistory(
                member = member,
                activity = activity,
            )

        memberActivityViewHistoryRepository.save(viewHistory)
    }
}
