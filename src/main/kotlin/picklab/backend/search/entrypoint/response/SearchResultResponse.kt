package picklab.backend.search.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.activity.application.model.ActivityItemWithBookmark

data class SearchResultResponse(
    @field:Schema(description = "검색 키워드")
    val keyword: String,
    @field:Schema(description = "전체 결과 수")
    val totalCount: Long,
    @field:Schema(description = "카테고리별 검색 결과 그룹")
    val groups: List<SearchCategoryGroup>,
)

data class SearchCategoryGroup(
    @field:Schema(description = "활동 유형 코드")
    val activityType: String,
    @field:Schema(description = "활동 유형명")
    val activityTypeName: String,
    @field:Schema(description = "해당 유형 결과 수")
    val count: Long,
    @field:Schema(description = "활동 목록")
    val items: List<ActivityItemWithBookmark>,
)
