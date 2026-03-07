package picklab.backend.member.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import picklab.backend.job.domain.JobCategoryRepository
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.member.application.MemberUseCase
import picklab.backend.member.domain.entity.InterestedJobCategory
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.repository.InterestedJobCategoryRepository
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.template.IntegrationTest
import java.time.LocalDate

class MemberMeServiceTest : IntegrationTest() {
    @Autowired
    lateinit var memberUseCase: MemberUseCase

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var interestedJobCategoryRepository: InterestedJobCategoryRepository

    @Autowired
    lateinit var jobCategoryRepository: JobCategoryRepository

    @BeforeEach
    fun setUp() {
        cleanUp.all()
    }

    @Test
    @DisplayName("getMemberMe는 요청한 회원 필드와 직무 데이터를 반환한다")
    fun getMemberMe() {
        val member =
            memberRepository.save(
                Member(
                    name = "hong",
                    email = "test@example.com",
                    nickname = "picklab_member",
                    educationLevel = "대학교(4년)",
                    birthDate = LocalDate.of(1998, 3, 1),
                    employmentStatus = "재직 중",
                    company = "Picklab",
                ),
            )

        val backend =
            jobCategoryRepository.save(
                JobCategory(
                    jobGroup = JobGroup.DEVELOPMENT,
                    jobDetail = JobDetail.BACKEND,
                ),
            )
        val devops =
            jobCategoryRepository.save(
                JobCategory(
                    jobGroup = JobGroup.DEVELOPMENT,
                    jobDetail = JobDetail.DEVOPS,
                ),
            )

        interestedJobCategoryRepository.saveAll(
            listOf(
                InterestedJobCategory(member, backend),
                InterestedJobCategory(member, devops),
            ),
        )

        val result = memberUseCase.getMemberMe(member.id)

        assertThat(result.name).isEqualTo("hong")
        assertThat(result.nickname).isEqualTo("picklab_member")
        assertThat(result.educationLevel).isEqualTo("대학교(4년)")
        assertThat(result.birthDate).isEqualTo(LocalDate.of(1998, 3, 1))
        assertThat(result.selectedInterestedJobs).containsExactly(JobDetail.BACKEND, JobDetail.DEVOPS)
        assertThat(result.jobFields).containsExactly(JobGroup.DEVELOPMENT)
        assertThat(result.employmentStatus).isEqualTo("재직 중")
        assertThat(result.company).isEqualTo("Picklab")
    }
}
