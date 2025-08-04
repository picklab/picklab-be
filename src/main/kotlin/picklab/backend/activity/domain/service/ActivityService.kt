package picklab.backend.activity.domain.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.application.ActivityQueryRepository
import picklab.backend.activity.application.model.ActivityItem
import picklab.backend.activity.application.model.ActivitySearchCondition
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import java.time.LocalDate

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val activityQueryRepository: ActivityQueryRepository,
) {
    /**
     * 활동 ID 값을 바탕으로 삭제되지 않은 활동을 반환합니다
     */
    fun mustFindById(activityId: Long): Activity =
        activityRepository
            .findById(activityId)
            .orElseThrow { throw BusinessException(ErrorCode.NOT_FOUND_ACTIVITY) }

    /**
     * 조건과 일치하는 활동 리스트를 페이징으로 가져옵니다
     */
    fun getActivities(
        queryData: ActivitySearchCondition,
        pageable: PageRequest,
    ) = activityRepository.getActivities(
        queryData = queryData,
        pageable = pageable,
    )

    /**
     * 활동 카테고리 별, 불필요한 데이터가 파라미터로 넘어올 경우 이를 제거하고 정의된 규격에 맞게 파라미터 값을 조정합니다
     */
    fun adjustQueryByCategory(query: ActivitySearchCondition): ActivitySearchCondition =
        when (query.category) {
            ActivityType.EXTRACURRICULAR -> {
                query.copy(
                    format = null,
                    costType = null,
                    award = null,
                    duration = null,
                    domain = null,
                )
            }

            ActivityType.SEMINAR -> {
                query.copy(
                    field = null,
                    format = null,
                    costType = null,
                    award = null,
                    duration = null,
                    domain = null,
                )
            }

            ActivityType.EDUCATION -> {
                var format = query.format
                if (format != null && format.contains(EducationFormatType.ALL)) {
                    format = listOf(EducationFormatType.ONLINE, EducationFormatType.OFFLINE)
                }

                val sanitizedDuration = sanitizeDurationRange(query.duration)

                query.copy(
                    field = null,
                    award = null,
                    domain = null,
                    format = format,
                    duration = sanitizedDuration,
                )
            }

            ActivityType.COMPETITION -> {
                val sanitizedAward = sanitizeAwardRange(query.award)

                query.copy(
                    field = null,
                    location = null,
                    format = null,
                    costType = null,
                    duration = null,
                    award = sanitizedAward,
                )
            }
        }

    /**
     * 기간의 범위가 임의로 들어 왔을 때 이를 조정합니다.
     */
    private fun sanitizeDurationRange(duration: List<Long>?): List<Long>? {
        val filteredDuration = duration?.filter { it >= 0 } ?: return null
        if (filteredDuration.isEmpty()) return null

        val min = filteredDuration.minOrNull()!!
        val max = filteredDuration.maxOrNull()!!

        return if (min == max) listOf(min) else listOf(min, max)
    }

    /**
     * 상금의 범위가 임의로 들어 왔을 때 이를 조정합니다
     */
    private fun sanitizeAwardRange(award: List<Long>?): List<Long>? {
        val filteredAward = award?.filter { it >= 0 } ?: return null
        if (filteredAward.isEmpty()) return null

        if (filteredAward.size == 1) {
            val curAward = filteredAward.maxOrNull() ?: throw IllegalStateException("max is null")
            return when {
                curAward < 5000000 -> listOf(0, 5000000)
                curAward >= 5000000 && curAward < 10000000 -> listOf(5000000, 10000000)
                else -> listOf(10000000, Long.MAX_VALUE)
            }
        }

        var min = filteredAward.minOrNull()!!
        var max = filteredAward.maxOrNull()!!

        if (min < 5000000) {
            min = 0
        } else if (min < 10000000) {
            min = 5000000
        } else {
            min = 10000000
        }

        if (max <= 5000000) {
            max = 5000000
        } else if (max <= 10000000) {
            max = 10000000
        } else {
            max = Long.MAX_VALUE
        }

        return listOf(min, max)
    }

    /**
     * 특정 마감일에 해당하는 모집 중인 활동들을 조회합니다
     */
    fun getActivitiesEndingOnDate(targetDate: LocalDate): List<Activity> =
        activityRepository.findByRecruitmentEndDateAndStatus(
            targetDate = targetDate,
            status = RecruitmentStatus.OPEN,
        )

    /**
     * 기준 날짜로부터 특정 일수 후의 마감일에 해당하는 모집 중인 활동들을 조회합니다
     */
    fun getActivitiesEndingInDays(
        baseDate: LocalDate,
        daysUntilDeadline: Int,
    ): List<Activity> {
        val targetDate = baseDate.plusDays(daysUntilDeadline.toLong())
        return getActivitiesEndingOnDate(targetDate)
    }

    /**
     * 현재 모집 중인 활동 중 가장 인기 있는 활동을 조회합니다.
     * 인기도는 조회수와 북마크 수를 합산하여 계산합니다.
     */
    fun getMostPopularActivity(): Activity? = activityRepository.findMostPopularActivity()

    /**
     * 특정 활동 ID 들로 ActivityItem을 조회합니다.
     */
    fun findActivityItemsByIds(activityIds: List<Long>): List<ActivityItem> =
        activityQueryRepository.findActivityItemByActivityIds(activityIds)

    /**
     * 활동명 자동완성 검색
     */
    @Transactional(readOnly = true)
    fun getActivityTitlesForAutocomplete(
        keyword: String,
        limit: Int,
    ): List<String> {
        val trimmedKeyword = keyword.trim()
        if (trimmedKeyword.isEmpty()) {
            return emptyList()
        }

        // limit 값 검증 및 제한 (1~50 사이로 제한)
        val validatedLimit = limit.coerceIn(1, 50)

        return activityRepository.findActivityTitlesForAutocomplete(trimmedKeyword, validatedLimit)
    }

    /**
     * 전체 활동 중 인기도가 높은 활동들을 조회합니다.
     * 인기도는 조회수와 북마크 수를 합산하여 계산합니다.
     */
    fun getPopularActivities(pageable: PageRequest): Page<ActivityItem> = activityQueryRepository.findPopularActivities(pageable)
}
