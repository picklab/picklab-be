package picklab.backend.review.entrypoint

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import picklab.backend.review.entrypoint.request.ReviewUpdateRequest
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ReviewUpdateRequestTest {
    private val contextRunner =
        ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration::class.java))
            .withPropertyValues("spring.jackson.property-naming-strategy=SNAKE_CASE")

    private val requestJson =
        """
        {
          "overall_score": 4,
          "info_score": 4,
          "difficulty_score": 4,
          "benefit_score": 4,
          "summary": "수정한 한줄평",
          "strength": "충분한 실습과 피드백으로 실제 업무에 필요한 경험을 쌓을 수 있었습니다.",
          "weakness": "활동 일정이 촉박하여 팀원들과 충분히 논의할 시간을 확보하기 어려웠습니다.",
          "job_relevance_score": 4,
          "object_key": "review/1/10/abcd1234_20261002_120000.jpg",
          "job_category": { "job_group": "DEVELOPMENT", "job_detail": null }
        }
        """.trimIndent()

    @Test
    fun `활동 ID 없이 수정 요청을 읽고 커맨드로 변환한다`() {
        contextRunner.run { context ->
            val mapper = context.getBean(ObjectMapper::class.java)
            val request = mapper.readValue<ReviewUpdateRequest>(requestJson)

            val command = request.toCommand(id = 99, memberId = 1)

            assertEquals(99L, command.id)
            assertEquals(1L, command.memberId)
            assertEquals("수정한 한줄평", command.summary)
            assertEquals("review/1/10/abcd1234_20261002_120000.jpg", command.objectKey)
            assertFalse(mapper.readTree(mapper.writeValueAsString(request)).has("activity_id"))
        }
    }

    @Test
    fun `기존 클라이언트가 보낸 활동 ID는 현재 Jackson 설정에서 무시한다`() {
        contextRunner.run { context ->
            val mapper = context.getBean(ObjectMapper::class.java)
            val expected = mapper.readValue<ReviewUpdateRequest>(requestJson)
            val body = mapper.readTree(requestJson) as ObjectNode
            body.put("activity_id", 999)

            val request = mapper.treeToValue(body, ReviewUpdateRequest::class.java)

            assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES))
            assertEquals(expected, request)
            assertEquals(expected.toCommand(99, 1), request.toCommand(99, 1))
        }
    }
}
