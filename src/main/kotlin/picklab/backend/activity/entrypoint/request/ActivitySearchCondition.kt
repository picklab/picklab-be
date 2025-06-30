package picklab.backend.activity.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema

data class ActivitySearchCondition(
    @Schema(description = "활동 분류 (extracurricular, seminar, education, competition)")
    val category: String,
    @Schema(description = "관련 직무")
    val jobTag: List<String>?,
    @Schema(description = "주최 기관")
    val organizer: List<String>?,
    @Schema(description = "참여 대상")
    val target: List<String>?,
    @Schema(description = "활동 분야")
    val field: List<String>?,
    @Schema(
        description =
            "모임 지역(all, seoul_incheon, gyeonggi_gangwon, daejeon_sejong_chungnam, " +
                "busan_daegu_gyeongsang, gwangju_jeolla, jeju, overseas)",
    )
    val location: List<String>?,
    @Schema(description = "온/오프라인 여부(all, online, offline)")
    val format: List<String>?,
    @Schema(description = "비용 유형(free, paid, fully_government, partially_government)")
    val costType: String?,
    @Schema(description = "최소 상금")
    val award: List<Long>?,
    @Schema(description = "최소 기간 (개월 단위, 최대 6)")
    val duration: List<Long>?,
    @Schema(description = "도메인(saas, web, app, cloud, ai, ...)")
    val domain: List<String>?,
    @Schema(description = "정렬 기준(recent, deadline, remaining)")
    val sort: String,
)
