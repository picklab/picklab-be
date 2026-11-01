package picklab.backend.auth.infrastructure

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import com.github.benmanes.caffeine.cache.Ticker
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime

class WithdrawnMemberTokenBlocklist(
    val retention: Duration,
    private val clock: Clock = Clock.systemDefaultZone(),
    ticker: Ticker = Ticker.systemTicker(),
) {
    private val blockedMembers =
        Caffeine
            .newBuilder()
            .ticker(ticker)
            .expireAfter(
                object : Expiry<Long, LocalDateTime> {
                    override fun expireAfterCreate(
                        key: Long,
                        value: LocalDateTime,
                        currentTime: Long,
                    ): Long = Duration.between(LocalDateTime.now(clock), value).toNanos().coerceAtLeast(0)

                    override fun expireAfterUpdate(
                        key: Long,
                        value: LocalDateTime,
                        currentTime: Long,
                        currentDuration: Long,
                    ): Long = expireAfterCreate(key, value, currentTime)

                    override fun expireAfterRead(
                        key: Long,
                        value: LocalDateTime,
                        currentTime: Long,
                        currentDuration: Long,
                    ): Long = currentDuration
                },
            ).build<Long, LocalDateTime>()

    fun block(
        memberId: Long,
        withdrawnAt: LocalDateTime,
    ) {
        val expiresAt = withdrawnAt.plus(retention)
        if (expiresAt.isAfter(LocalDateTime.now(clock))) {
            blockedMembers.put(memberId, expiresAt)
        }
    }

    fun isBlocked(memberId: Long): Boolean = blockedMembers.getIfPresent(memberId) != null
}
