package picklab.backend.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {
    @Bean("oAuthRestClient")
    fun oAuthRestClient(): RestClient {
        val requestFactory =
            SimpleClientHttpRequestFactory().apply {
                setConnectTimeout(3000)
                setReadTimeout(10000)
            }
        return RestClient
            .builder()
            .requestFactory(requestFactory)
            .build()
    }
}
