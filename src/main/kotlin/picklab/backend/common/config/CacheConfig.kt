package picklab.backend.common.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig {
    companion object {
        const val CACHE_MIN_DURATION = 5L
        const val MAXIMUM_CACHE_SIZE = 10000L
    }

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager("activityViewCount")
        cacheManager.setCaffeine(
            Caffeine
                .newBuilder()
                .expireAfterWrite(CACHE_MIN_DURATION, TimeUnit.MINUTES)
                .maximumSize(MAXIMUM_CACHE_SIZE),
        )

        return cacheManager
    }
}
