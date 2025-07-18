package picklab.backend.activity.infrastructure

import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component
import picklab.backend.activity.application.ViewCountLimiterPort
import picklab.backend.activity.application.ViewCountLimiterPort.Companion.CACHE_NAME
import picklab.backend.activity.application.ViewCountLimiterPort.Companion.MAX_VIEW_ATTEMPTS

/**
 * ViewCountLimiterPort의 구현체. 추후 Redis 등의 외부 캐싱으로 전환될 가능성이 있어 별도의 구현체를 가지는 어댑터 패턴 적용
 */
@Component
class ViewCountLimiterAdapter(
    private val cacheManager: CacheManager,
) : ViewCountLimiterPort {
    override fun isViewCountUpAllowed(
        activityId: Long,
        viewerIdentifier: String,
    ): Boolean {
        val cacheKey = "activity:$activityId:$viewerIdentifier"
        val cache = cacheManager.getCache(CACHE_NAME) ?: return true

        val viewAttempt = cache.get(cacheKey) { 0 } ?: 0

        if (viewAttempt < MAX_VIEW_ATTEMPTS) {
            cache.put(cacheKey, viewAttempt + 1)
            return true
        }

        return false
    }
}
