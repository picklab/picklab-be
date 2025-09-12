package picklab.backend.template

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import picklab.backend.helper.CleanUp

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestContainerConfig::class, TestS3Config::class)
abstract class IntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var mapper: ObjectMapper

    @Autowired
    protected lateinit var cleanUp: CleanUp
}
