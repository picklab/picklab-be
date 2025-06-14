package picklab.backend.member.entrypoint.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import picklab.backend.member.domain.enums.EmploymentType

data class AdditionalInfoRequest(
    @field:NotBlank
    @field:Pattern(
        regexp = "^[a-zA-Z0-9가-힣_.-]{1,20}$",
        message = "닉네임은 영문, 숫자, 한글, _, -, .만 사용 가능하며 최대 20자까지 입력 가능합니다.",
    )
    @field:Schema(description = "회원 닉네임(필수)", example = "picklab멤버", maxLength = 20)
    val nickname: String,
    @field:NotBlank
    @field:JsonProperty("education_level")
    @field:Schema(description = "최종 학력(필수)", example = "대학교(4년)")
    val educationLevel: String,
    @field:NotBlank
    @field:Schema(description = "학교명(필수)", example = "서울테스트학교")
    val school: String,
    @field:NotBlank
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
    @field:NotEmpty
    @field:JsonProperty("interested_job_categories")
    val interestedJobCategories: List<JobCategoryDto>,
)
