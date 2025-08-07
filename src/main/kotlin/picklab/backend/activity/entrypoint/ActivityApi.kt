package picklab.backend.activity.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.entrypoint.request.ActivitySearchRequest
import picklab.backend.activity.entrypoint.request.GetActivityPageRequest
import picklab.backend.activity.entrypoint.response.GetActivityDetailResponse
import picklab.backend.activity.entrypoint.response.GetActivityListResponse
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper

@Tag(name = "활동 API", description = "활동 관련 API")
interface ActivityApi {
    @Operation(
        summary = "활동 조회",
        description =
            "필터 조건에 맞는 활동을 조회합니다.\n\n" +
                "각 활동 분야에 따라 불필요한 parameter가 들어올 경우 null로 처리되어 항상 200 OK 응답을 반환하도록 합니다.\n\n" +
                "(ex. 대외활동 조회 시 award, duration 등은 요청값으로 들어오더라도 null로 처리)\n\n" +
                "조회 데이터가 없는 경우 빈 리스트를 반환합니다.\n\n" +
                "award, duration 의 경우는 List 형태로 요청값을 입력받으며 최소와 최대를 제한해야 하는 경우\n\n" +
                "/v1/activities?award=1000000&award=5000000 의 형태로 입력받습니다.\n\n" +
                "/v1/activities?award=5000000의 형태로 단일값의 경우 최대 상금(교육 기간)으로 판단합니다.\n\n" +
                "**요청 파라미터:**\n" +
                "- category: 활동 분류 (extracurricular, seminar, education, competition)\n" +
                "- jobTag: 직무 태그 (ex. frontend, backend)\n" +
                "- organizer: 주최 기관 (large_corporation, medium_corporation, small_corporation, public_organization, foreign_corporation, non_profit, startup, financial_institution, hospital, etc.)\n" +
                "- target: 참여 대상(all, university_student, worker)\n" +
                "- field: 분야 (ex. supporters, marketer, mentoring, press, overseas_volunteer, domestic_volunteer)\n" +
                "- location: 모임 지역 (all, seoul_incheon, gyeonggi_gangwon, daejeon_sejong_chungnam, busan_daegu_gyeongsang, gwangju_jeolla, jeju, overseas)\n" +
                "- format: 온/오프라인 여부 (all, online, offline)\n" +
                "- costType: 비용 유형 (free, paid, fully_government, partially_government)\n" +
                "- award: 상금 (공모전/해커톤, List 형태)\n" +
                "- duration: 교육 기간 (교육, List 형태)\n" +
                "- domain: 도메인 (saas, web, app, cloud, ai 등)\n" +
                "- size: 한번에 가져올 데이터 개수 (기본값 20)\n" +
                "- sort: 정렬 조건 (latest / deadline_asc / deadline_desc)\n" +
                "- page: 페이지 번호 (기본값 1)",
        responses = [
            ApiResponse(responseCode = "200", description = "활동 조회에 성공했습니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun getActivities(
        @ModelAttribute condition: ActivitySearchRequest,
        @Parameter(description = "데이터 개수")
        @RequestParam(defaultValue = "20") size: Int,
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<ResponseWrapper<GetActivityListResponse>>

    @Operation(
        summary = "활동 페이지 상세 조회",
        description = "특정 ID값의 활동 페이지를 조회합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "활동 조회에 성공했습니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun getActivitiesDetail(
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<GetActivityDetailResponse>>

    @Operation(
        summary = "활동 조회수 증가",
        description = "해당 활동의 조회수를 1 증가시킵니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "조회수 증가에 성공했습니다."),
            ApiResponse(responseCode = "404", description = "해당 활동을 찾을 수 없습니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun increaseViewCount(
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
        request: HttpServletRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "직무 추천 활동 조회",
        description = "직무 관련 추천 활동을 조회할 수 있다",
        responses = [
            ApiResponse(responseCode = "200", description = "직무 추천 활동 조회에 성공했습니다."),
        ],
    )
    fun getRecommendationActivities(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @ModelAttribute request: GetActivityPageRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityItemWithBookmark>>>

    @Operation(
        summary = "인기 활동 조회",
        description = "전체 활동 중 조회수와 북마크 수를 합산한 인기도 순으로 활동을 조회합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "인기 활동 조회에 성공했습니다."),
        ],
    )
    fun getWeeklyPopularActivities(
        @Valid @ModelAttribute request: GetActivityPageRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityItemWithBookmark>>>
}
