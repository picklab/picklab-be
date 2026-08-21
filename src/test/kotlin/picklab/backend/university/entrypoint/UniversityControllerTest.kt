package picklab.backend.university.entrypoint

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
import picklab.backend.template.IntegrationTest
import picklab.backend.university.domain.entity.University
import picklab.backend.university.domain.repository.UniversityRepository
import picklab.backend.university.entrypoint.response.UniversitySearchResponse
import kotlin.test.Test

class UniversityControllerTest : IntegrationTest() {
    @Autowired
    lateinit var universityRepository: UniversityRepository

    @BeforeEach
    fun setUp() {
        cleanUp.all()
    }

    @Nested
    @DisplayName("대학교 검색")
    inner class Search {
        @Test
        @DisplayName("[성공] 인증 없이 학교명을 포함 검색한다.")
        fun searchUniversitiesWithoutAuthentication() {
            val seoulNational = universityRepository.save(University(name = "서울대학교"))
            val seoulCity = universityRepository.save(University(name = "서울시립대학교"))
            universityRepository.save(University(name = "한양대학교"))
            universityRepository.save(University(name = "서울비노출대학교", isActive = false))

            val result =
                mockMvc
                    .get("/v1/universities") {
                        param("query", "서울")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<UniversitySearchResponse> = result.extractBody(mapper)

            assertThat(body.code).isEqualTo(SuccessCode.UNIVERSITIES_RETRIEVED.status.value())
            assertThat(body.message).isEqualTo(SuccessCode.UNIVERSITIES_RETRIEVED.message)
            assertThat(body.data!!.items)
                .extracting("id", "name")
                .containsExactly(
                    tuple(seoulNational.id, "서울대학교"),
                    tuple(seoulCity.id, "서울시립대학교"),
                )
        }

        @Test
        @DisplayName("[성공] 검색어 앞뒤 공백을 제거한다.")
        fun trimQuery() {
            universityRepository.save(University(name = "한국과학기술원"))
            universityRepository.save(University(name = "서울과학기술대학교"))

            val result =
                mockMvc
                    .get("/v1/universities") {
                        param("query", " 과학 ")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<UniversitySearchResponse> = result.extractBody(mapper)

            assertThat(body.data!!.items)
                .extracting("name")
                .containsExactly("한국과학기술원", "서울과학기술대학교")
        }

        @Test
        @DisplayName("[성공] 앞부분 일치 결과를 포함 일치 결과보다 먼저 반환한다.")
        fun prioritizePrefixMatches() {
            universityRepository.save(University(name = "남서울대학교"))
            universityRepository.save(University(name = "서울과학기술대학교"))
            universityRepository.save(University(name = "서울대학교"))
            universityRepository.save(University(name = "서울시립대학교"))

            val result =
                mockMvc
                    .get("/v1/universities") {
                        param("query", "서울대")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<UniversitySearchResponse> = result.extractBody(mapper)

            assertThat(body.data!!.items)
                .extracting("name")
                .containsExactly("서울대학교", "남서울대학교")
        }

        @Test
        @DisplayName("[성공] 같은 일치 유형에서는 짧은 학교명을 먼저 반환한다.")
        fun prioritizeShorterNamesInSameMatchType() {
            universityRepository.save(University(name = "서울과학기술대학교"))
            universityRepository.save(University(name = "서울교육대학교"))
            universityRepository.save(University(name = "서울대학교"))
            universityRepository.save(University(name = "서울시립대학교"))

            val result =
                mockMvc
                    .get("/v1/universities") {
                        param("query", "서울")
                        param("size", "3")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<UniversitySearchResponse> = result.extractBody(mapper)

            assertThat(body.data!!.items)
                .extracting("name")
                .containsExactly("서울대학교", "서울교육대학교", "서울시립대학교")
        }

        @Test
        @DisplayName("[성공] 검색어가 없거나 공백이면 빈 목록을 반환한다.")
        fun blankQueryReturnsEmptyItems() {
            universityRepository.save(University(name = "서울대학교"))

            val result =
                mockMvc
                    .get("/v1/universities") {
                        param("query", "   ")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<UniversitySearchResponse> = result.extractBody(mapper)

            assertThat(body.data!!.items).isEmpty()
        }

        @Test
        @DisplayName("[성공] size가 20을 초과하면 20개로 보정한다.")
        fun clampSizeToMax() {
            (1..25).forEach { index ->
                universityRepository.save(University(name = "테스트${index.toString().padStart(2, '0')}대학교"))
            }

            val result =
                mockMvc
                    .get("/v1/universities") {
                        param("query", "테스트")
                        param("size", "100")
                    }.andExpect { status { isOk() } }
                    .andReturn()

            val body: ResponseWrapper<UniversitySearchResponse> = result.extractBody(mapper)

            assertThat(body.data!!.items).hasSize(20)
        }

        @Test
        @DisplayName("[실패] size가 0 이하면 잘못된 요청으로 응답한다.")
        fun failWhenSizeIsLessThanOrEqualToZero() {
            mockMvc
                .get("/v1/universities") {
                    param("query", "서울")
                    param("size", "0")
                }.andExpect { status { isBadRequest() } }
        }
    }
}
