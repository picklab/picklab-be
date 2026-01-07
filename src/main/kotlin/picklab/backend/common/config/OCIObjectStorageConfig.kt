package picklab.backend.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class OCIObjectStorageConfig(
    @Value("\${oci.cloud.access-key}")
    private val accessKey: String,
    @Value("\${oci.cloud.secret-key}")
    private val secretKey: String,
    @Value("\${oci.cloud.region}")
    private val region: String,
    @Value("\${oci.cloud.end-point}")
    private val endpoint: String,
) {
    @Bean
    fun ociObjectStoragePresigner(): S3Presigner {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Presigner
            .builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .serviceConfiguration(
                S3Configuration
                    .builder()
                    .pathStyleAccessEnabled(true)
                    .build(),
            ).build()
    }

    @Bean
    fun ociObjectStorageClient(): S3Client {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Client
            .builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .serviceConfiguration(
                S3Configuration
                    .builder()
                    .pathStyleAccessEnabled(true)
                    .build(),
            ).build()
    }
}
