package picklab.backend.activity.domain.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import picklab.backend.activity.application.model.ActivitySearchCommand
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.notification.domain.config.NotificationDeadlineProperties
import java.time.LocalDate
import java.time.ZoneId

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val notificationDeadlineProperties: NotificationDeadlineProperties,
) {
    fun mustFindById(activityId: Long): Activity =
        activityRepository
            .findById(activityId)
            .orElseThrow { throw BusinessException(ErrorCode.NOT_FOUND_ACTIVITY) }

    fun mustFindActiveActivity(activityId: Long): Activity =
        activityRepository
            .findById(activityId)
            .orElseThrow { throw BusinessException(ErrorCode.NOT_FOUND_ACTIVITY) }

    fun getActivities(
        queryData: ActivitySearchCommand,
        pageable: PageRequest,
    ) = activityRepository.getActivities(
        queryData = queryData,
        pageable = pageable,
    )

    fun adjustQueryByCategory(query: ActivitySearchCommand): ActivitySearchCommand =
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

    private fun sanitizeDurationRange(duration: List<Long>?): List<Long>? {
        val filteredDuration = duration?.filter { it >= 0 } ?: return null
        if (filteredDuration.isEmpty()) return null

        val min = filteredDuration.minOrNull()!!
        val max = filteredDuration.maxOrNull()!!

        return if (min == max) listOf(min) else listOf(min, max)
    }

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
     * 설정된 시간대 기준으로 현재 날짜를 반환합니다
     */
    fun getCurrentDateInKST(): LocalDate {
        return LocalDate.now(ZoneId.of(notificationDeadlineProperties.timezone))
    }

    /**
     * 특정 일수 후의 마감일에 해당하는 모집 중인 활동들을 조회합니다 (설정된 시간대 기준)
     */
    fun getActivitiesEndingInDays(daysUntilDeadline: Int): List<Activity> {
        val targetDate = getCurrentDateInKST().plusDays(daysUntilDeadline.toLong())
        return activityRepository.findByRecruitmentEndDateAndStatus(
            targetDate = targetDate,
            status = RecruitmentStatus.OPEN
        )
    }
}
