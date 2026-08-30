package picklab.backend.highschool.entrypoint

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.extractBody
import picklab.backend.highschool.domain.entity.HighSchool
import picklab.backend.highschool.domain.repository.HighSchoolRepository
import picklab.backend.highschool.entrypoint.response.HighSchoolSearchResponse
import picklab.backend.template.IntegrationTest
import kotlin.test.Test

class HighSchoolControllerTest : IntegrationTest() {
    @Autowired
    lateinit var highSchoolRepository: HighSchoolRepository

    @BeforeEach
    fun setUp() {
        cleanUp.all()
    }

    @Nested
    @DisplayName("고등학교 검색")
    inner class Search {
        @Test
        @DisplayName("[성공] 인증 없이 학교명을 포함 검색한다.")
        fun searchHighSchoolsWithoutAuthentication() {
            val seoul = highSchoolRepository.save(HighSchool(name = "서울고등학교"))
            val seoulGirls = highSchoolRepository.save(HighSchool(name = "서울여자고등학교"))
            highSchoolRepository.save(HighSchool(name = "한양고등학교"))
            highSchoolRepository.save(HighSchool(name = "서울비노출고등학교", isActive = false))

            val result =
                mockMvc
                    .get("/v1/high-schools") {
                        param("query", "서울")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<HighSchoolSearchResponse> = result.extractBody(mapper)

            assertThat(body.code).isEqualTo(SuccessCode.HIGH_SCHOOLS_RETRIEVED.status.value())
            assertThat(body.message).isEqualTo(SuccessCode.HIGH_SCHOOLS_RETRIEVED.message)
            assertThat(body.data!!.items)
                .extracting("id", "name")
                .containsExactly(
                    tuple(seoul.id, "서울고등학교"),
                    tuple(seoulGirls.id, "서울여자고등학교"),
                )
        }

        @Test
        @DisplayName("[성공] 검색어 앞뒤 공백을 제거한다.")
        fun trimQuery() {
            highSchoolRepository.save(HighSchool(name = "한국과학영재학교"))
            highSchoolRepository.save(HighSchool(name = "서울과학고등학교"))

            val result =
                mockMvc
                    .get("/v1/high-schools") {
                        param("query", " 과학 ")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<HighSchoolSearchResponse> = result.extractBody(mapper)

            assertThat(body.data!!.items)
                .extracting("name")
                .containsExactly("서울과학고등학교", "한국과학영재학교")
        }

        @Test
        @DisplayName("[성공] 앞부분 일치 결과를 포함 일치 결과보다 먼저 반환한다.")
        fun prioritizePrefixMatches() {
            highSchoolRepository.save(HighSchool(name = "남서울고등학교"))
            highSchoolRepository.save(HighSchool(name = "서울고등학교"))

            val result =
                mockMvc
                    .get("/v1/high-schools") {
                        param("query", "서울고")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<HighSchoolSearchResponse> = result.extractBody(mapper)

            assertThat(body.data!!.items)
                .extracting("name")
                .containsExactly("서울고등학교", "남서울고등학교")
        }

        @Test
        @DisplayName("[성공] 검색어가 없거나 공백이면 빈 목록을 반환한다.")
        fun blankQueryReturnsEmptyItems() {
            highSchoolRepository.save(HighSchool(name = "서울고등학교"))

            val result =
                mockMvc
                    .get("/v1/high-schools") {
                        param("query", "   ")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<HighSchoolSearchResponse> = result.extractBody(mapper)

            assertThat(body.data!!.items).isEmpty()
        }

        @Test
        @DisplayName("[성공] size가 20을 초과하면 20개로 보정한다.")
        fun clampSizeToMax() {
            (1..25).forEach { index ->
                highSchoolRepository.save(HighSchool(name = "테스트${index.toString().padStart(2, '0')}고등학교"))
            }

            val result =
                mockMvc
                    .get("/v1/high-schools") {
                        param("query", "테스트")
                        param("size", "100")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<HighSchoolSearchResponse> = result.extractBody(mapper)

            assertThat(body.data!!.items).hasSize(20)
        }

        @Test
        @DisplayName("[실패] size가 0 이하면 잘못된 요청으로 응답한다.")
        fun failWhenSizeIsLessThanOrEqualToZero() {
            mockMvc
                .get("/v1/high-schools") {
                    param("query", "서울")
                    param("size", "0")
                }.andExpect { status { isBadRequest() } }
        }
    }
}
