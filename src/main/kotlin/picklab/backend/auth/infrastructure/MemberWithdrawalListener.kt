package picklab.backend.auth.infrastructure

import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener
import picklab.backend.member.domain.MemberWithdrawnEvent
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.notification.domain.service.SseEmitterService
import java.time.LocalDateTime

@Component
class MemberWithdrawalListener(
    private val blocklist: WithdrawnMemberTokenBlocklist,
    private val memberRepository: MemberRepository,
    private val sseEmitterService: SseEmitterService,
) : SmartInitializingSingleton {
    override fun afterSingletonsInstantiated() {
        memberRepository
            .findWithdrawnSince(LocalDateTime.now().minus(blocklist.retention))
            .forEach { blocklist.block(it.id, it.deletedAt) }
    }

    @TransactionalEventListener
    fun onMemberWithdrawn(event: MemberWithdrawnEvent) {
        blocklist.block(event.memberId, event.withdrawnAt)
        sseEmitterService.disconnect(event.memberId)
    }
}
