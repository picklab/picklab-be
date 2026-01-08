package picklab.backend.template

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@TestConfiguration(proxyBeanMethods = false)
class TestS3Config(
    @Value("\${oci.cloud.access-key}")
    private val accessKey: String,
    @Value("\${oci.cloud.secret-key}")
    private val secretKey: String,
    @Value("\${oci.cloud.region:us-phoenix-1}")
    private val region: String,
) {
    @Bean
    @Primary
    fun testOciObjectStorageClient(awsS3Container: LocalStackContainer): S3Client =
        S3Client
            .builder()
            .endpointOverride(awsS3Container.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey),
                ),
            ).region(Region.of(region))
            .serviceConfiguration(
                S3Configuration
                    .builder()
                    .pathStyleAccessEnabled(true)
                    .build(),
            ).build()

    @Bean
    @Primary
    fun testOciObjectStoragePresigner(awsS3Container: LocalStackContainer): S3Presigner =
        S3Presigner
            .builder()
            .endpointOverride(awsS3Container.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey),
                ),
            ).region(Region.of(region))
            .serviceConfiguration(
                S3Configuration
                    .builder()
                    .pathStyleAccessEnabled(true)
                    .build(),
            ).build()
}
