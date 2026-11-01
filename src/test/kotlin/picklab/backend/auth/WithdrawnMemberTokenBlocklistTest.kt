package picklab.backend.auth

import com.github.benmanes.caffeine.cache.Ticker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import picklab.backend.auth.infrastructure.WithdrawnMemberTokenBlocklist
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class WithdrawnMemberTokenBlocklistTest {
    private val time = TestTime()
    private val retention = Duration.ofHours(1)
    private val blocklist = WithdrawnMemberTokenBlocklist(retention, time, time)

    @Test
    fun blocksOnlyWithdrawnMemberUntilTokenLifetimeEnds() {
        blocklist.block(1, LocalDateTime.now(time))
        assertThat(blocklist.isBlocked(1)).isTrue()
        assertThat(blocklist.isBlocked(2)).isFalse()

        time.advance(retention.minusNanos(1))
        assertThat(blocklist.isBlocked(1)).isTrue()
        time.advance(Duration.ofNanos(1))
        assertThat(blocklist.isBlocked(1)).isFalse()
    }

    @Test
    fun restoresOnlyRemainingLifetimeWithoutExtendingOnReads() {
        val withdrawnAt = LocalDateTime.now(time).minusMinutes(55)
        blocklist.block(1, withdrawnAt)

        time.advance(Duration.ofMinutes(4))
        assertThat(blocklist.isBlocked(1)).isTrue()
        blocklist.block(1, withdrawnAt)
        time.advance(Duration.ofMinutes(1))
        assertThat(blocklist.isBlocked(1)).isFalse()
    }

    @Test
    fun doesNotRestoreWithdrawalsWhoseTokensHaveExpired() {
        blocklist.block(1, LocalDateTime.now(time).minus(retention))
        blocklist.block(2, LocalDateTime.now(time).minusDays(1))

        assertThat(blocklist.isBlocked(1)).isFalse()
        assertThat(blocklist.isBlocked(2)).isFalse()
    }

    @Test
    fun doesNotEvictBlocksAtTheExistingViewCountCacheSizeLimit() {
        val withdrawnAt = LocalDateTime.now(time)
        (1L..10001L).forEach { blocklist.block(it, withdrawnAt) }

        assertThat((1L..10001L).all { blocklist.isBlocked(it) }).isTrue()
    }

    private class TestTime :
        Clock(),
        Ticker {
        private val start = Instant.parse("2026-11-01T00:00:00Z")
        private var current = start

        override fun getZone(): ZoneId = ZoneOffset.UTC

        override fun withZone(zone: ZoneId): Clock = Clock.fixed(current, zone)

        override fun instant(): Instant = current

        override fun read(): Long = Duration.between(start, current).toNanos()

        fun advance(duration: Duration) {
            current = current.plus(duration)
        }
    }
}
