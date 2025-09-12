package picklab.backend.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class NCPObjectStorageConfig(
    @Value("\${ncp.cloud.access-key}")
    private val accessKey: String,
    @Value("\${ncp.cloud.secret-key}")
    private val secretKey: String,
    @Value("\${ncp.cloud.region:kr-standard}")
    private val region: String,
    @Value("\${ncp.cloud.end-point:https://kr.object.ncloudstorage.com}")
    private val endpoint: String,
) {
    @Bean
    fun ncpObjectStoragePresigner(): S3Presigner {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Presigner
            .builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .build()
    }

    @Bean
    fun ncpObjectStorageClient(): S3Client {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Client
            .builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .build()
    }
}
