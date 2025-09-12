package picklab.backend.template

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestContainerConfig {
    @Bean
    @ServiceConnection
    fun mysqlContainer(): MySQLContainer<*> = MySQLContainer(DockerImageName.parse("mysql:8.0"))

    @Bean
    fun awsS3Container(): LocalStackContainer =
        LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(LocalStackContainer.Service.S3)
            .also { it.start() }
}
