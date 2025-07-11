package picklab.backend.member.entrypoint.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.member.domain.enums.EmploymentType

data class AdditionalInfoRequest(
    @field:NotBlank(message = "닉네임은 필수 입력값입니다.")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9가-힣_.-]{1,20}$",
        message = "닉네임은 영문, 숫자, 한글, _, -, .만 사용 가능하며 최대 20자까지 입력 가능합니다.",
    )
    @field:Schema(description = "회원 닉네임(필수)", example = "picklab멤버", maxLength = 20)
    val nickname: String,
    @field:NotBlank(message = "최종 학력은 필수 입력값입니다.")
    @field:JsonProperty("education_level")
    @field:Schema(description = "최종 학력(필수)", example = "대학교(4년)")
    val educationLevel: String,
    @field:NotBlank(message = "학교명은 필수 입력값입니다.")
    @field:Schema(description = "학교명(필수)", example = "서울테스트학교")
    val school: String,
    @field:NotBlank(message = "전공은 필수 입력값입니다.")
    @field:JsonProperty("graduation_status")
    @field:Schema(description = "졸업여부(필수)", example = "졸업")
    val graduationStatus: String,
    @field:JsonProperty("employment_status")
    @field:Schema(description = "재직상태(선택)", example = "재직 중")
    val employmentStatus: String = "",
    @field:Schema(description = "회사명", example = "테스트기업")
    val company: String = "",
    @field:JsonProperty("employment_type")
    @field:Schema(description = "고용 형태(선택)", example = "NONE")
    val employmentType: EmploymentType = EmploymentType.NONE,
    @field:NotEmpty(message = "관심 직군은 필수 입력값입니다.")
    @field:JsonProperty("interested_job_categories")
    @field:Schema(description = "관심 직무 카테고리")
    val interestedJobCategories: List<JobCategoryDto>,
){
    fun toJobGroupDetailMap(): List<Pair<JobGroup, JobDetail>> = this.interestedJobCategories.map {
        JobGroup.valueOf(it.group) to JobDetail.valueOf(it.detail)
    }
}
