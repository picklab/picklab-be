package picklab.backend.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import picklab.backend.auth.infrastructure.MemberWithdrawalListener
import picklab.backend.auth.infrastructure.WithdrawnMemberTokenBlocklist
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.notification.domain.service.SseEmitterService
import java.time.Duration
import java.time.LocalDateTime

class MemberWithdrawalListenerTest {
    @Test
    fun restorationFailurePreventsStartupFromCompleting() {
        val repository = mockk<MemberRepository>()
        every { repository.findWithdrawnSince(any()) } throws IllegalStateException("DB unavailable")
        val listener =
            MemberWithdrawalListener(
                WithdrawnMemberTokenBlocklist(Duration.ofHours(1)),
                repository,
                mockk<SseEmitterService>(),
            )

        assertThrows<IllegalStateException> { listener.afterSingletonsInstantiated() }
    }

    @Test
    fun restoresRecentWithdrawalsBeforeStartupCompletes() {
        val repository = mockk<MemberRepository>()
        val withdrawnAt = LocalDateTime.now().minusMinutes(5)
        val withdrawal =
            mockk<MemberRepository.WithdrawnMember> {
                every { id } returns 10L
                every { deletedAt } returns withdrawnAt
            }
        every { repository.findWithdrawnSince(any()) } returns listOf(withdrawal)
        val blocklist = WithdrawnMemberTokenBlocklist(Duration.ofHours(1))
        val listener = MemberWithdrawalListener(blocklist, repository, mockk<SseEmitterService>())

        listener.afterSingletonsInstantiated()

        assertThat(blocklist.isBlocked(10)).isTrue()
        assertThat(blocklist.isBlocked(11)).isFalse()
        verify(exactly = 1) { repository.findWithdrawnSince(any()) }
    }
}
