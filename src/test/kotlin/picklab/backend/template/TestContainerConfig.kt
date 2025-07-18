package picklab.backend.template

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestContainerConfig {
    @Bean
    @ServiceConnection
    fun mysqlContainer(): MySQLContainer<*> = MySQLContainer(DockerImageName.parse("mysql:8.0"))
}
