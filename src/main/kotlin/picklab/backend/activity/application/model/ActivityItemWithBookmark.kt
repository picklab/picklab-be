package picklab.backend.activity.application.model

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.activity.domain.enums.RecruitmentEndType
import java.time.LocalDate

data class ActivityItemWithBookmark(
    @field:Schema(description = "활동 ID")
    val id: Long,
    @field:Schema(description = "활동명")
    val title: String,
    @field:Schema(description = "주최기관/단체명")
    val organization: String?,
    @field:Schema(description = "주최기관 유형")
    val organizerType: String,
    @field:Schema(description = "활동 시작일")
    val startDate: LocalDate,
    @field:Schema(description = "활동 유형 (EXTRACURRICULAR, COMPETITION, SEMINAR, EDUCATION)")
    val category: String,
    @field:Schema(description = "직무 태그 목록")
    val jobTags: List<String>,
    @field:Schema(description = "활동 썸네일 이미지 URL")
    val thumbnailUrl: String?,
    @field:Schema(description = "조회수")
    val viewCount: Long,
    @field:Schema(description = "모집 종료 유형 (FIXED: 날짜 지정, ALWAYS_OPEN: 상시모집, CLOSE_ON_HIRE: 채용시마감)")
    val recruitmentEndType: RecruitmentEndType,
    @field:Schema(description = "모집 마감까지 남은 일수 (상시모집·채용시마감은 null)")
    val dDay: Long?,
    @field:Schema(description = "북마크 여부")
    val isBookmarked: Boolean,
)
