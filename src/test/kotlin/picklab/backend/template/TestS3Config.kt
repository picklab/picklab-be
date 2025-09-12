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
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@TestConfiguration(proxyBeanMethods = false)
class TestS3Config(
    @Value("\${ncp.cloud.access-key}")
    private val accessKey: String,
    @Value("\${ncp.cloud.secret-key}")
    private val secretKey: String,
    @Value("\${ncp.cloud.region:kr-standard}")
    private val region: String,
) {
    @Bean
    @Primary
    fun testNcpObjectStorageClient(awsS3Container: LocalStackContainer): S3Client =
        S3Client
            .builder()
            .endpointOverride(awsS3Container.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey),
                ),
            ).region(Region.of(region))
            .forcePathStyle(true)
            .build()

    @Bean
    @Primary
    fun testNcpObjectStoragePresigner(awsS3Container: LocalStackContainer): S3Presigner =
        S3Presigner
            .builder()
            .endpointOverride(awsS3Container.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey),
                ),
            ).region(Region.of(region))
            .build()
}
