package picklab.backend.activity.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.DomainType
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.job.domain.enums.JobDetail

data class ActivitySearchRequest(
    @field:Schema(description = "활동 분류")
    val category: ActivityType,
    @field:Schema(description = "관련 직무")
    val jobTag: List<JobDetail>?,
    @field:Schema(description = "주최 기관")
    val organizer: List<OrganizerType>?,
    @field:Schema(description = "참여 대상")
    val target: List<ParticipantType>?,
    @field:Schema(description = "활동 분야")
    val field: List<ActivityFieldType>?,
    @field:Schema(description = "모임 지역")
    val location: List<LocationType>?,
    @field:Schema(description = "온/오프라인 여부")
    val format: List<EducationFormatType>?,
    @field:Schema(description = "비용 유형")
    val costType: EducationCostType?,
    @field:Schema(description = "최소 상금")
    val award: List<Long>?,
    @field:Schema(description = "최소 기간 (개월 단위, 최대 6)")
    val duration: List<Long>?,
    @field:Schema(description = "도메인")
    val domain: List<DomainType>?,
    @field:Schema(description = "정렬 기준")
    val sort: ActivitySortType,
)
