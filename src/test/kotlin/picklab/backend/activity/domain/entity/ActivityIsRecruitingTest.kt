package picklab.backend.activity.domain.entity

import org.junit.jupiter.api.DisplayName
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activitygroup.domain.entity.ActivityGroup
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActivityIsRecruitingTest {
    private val today = LocalDate.of(2026, 5, 5)

    private fun activity(
        recruitmentEndDate: LocalDate?,
        status: RecruitmentStatus = RecruitmentStatus.OPEN,
    ) = ExternalActivity(
        title = "테스트 활동",
        organizer = OrganizerType.LARGE_CORPORATION,
        targetAudience = ParticipantType.ALL,
        location = LocationType.ALL,
        recruitmentStartDate = LocalDate.of(2026, 1, 1),
        recruitmentEndDate = recruitmentEndDate,
        startDate = LocalDate.of(2026, 6, 1),
        endDate = null,
        status = status,
        viewCount = 0L,
        duration = 30,
        activityHomepageUrl = null,
        activityApplicationUrl = null,
        activityThumbnailUrl = null,
        description = null,
        benefit = "",
        activityGroup = ActivityGroup(name = "테스트 그룹", description = ""),
        activityField = ActivityFieldType.SUPPORTERS,
    )

    @Test
    @DisplayName("마감일이 오늘 이후면 모집 중")
    fun endDateAfterTodayIsRecruiting() {
        assertTrue(activity(today.plusDays(1)).isRecruiting(today))
    }

    @Test
    @DisplayName("마감일이 오늘이면 모집 중")
    fun endDateTodayIsRecruiting() {
        assertTrue(activity(today).isRecruiting(today))
    }

    @Test
    @DisplayName("마감일이 지나면 모집 종료")
    fun endDateBeforeTodayIsNotRecruiting() {
        assertFalse(activity(today.minusDays(1)).isRecruiting(today))
    }

    @Test
    @DisplayName("상시모집(null)이고 status가 OPEN이면 모집 중")
    fun nullEndDateWithOpenStatusIsRecruiting() {
        assertTrue(activity(recruitmentEndDate = null, status = RecruitmentStatus.OPEN).isRecruiting(today))
    }

    @Test
    @DisplayName("상시모집(null)이어도 status가 CLOSED이면 모집 종료")
    fun nullEndDateWithClosedStatusIsNotRecruiting() {
        assertFalse(activity(recruitmentEndDate = null, status = RecruitmentStatus.CLOSED).isRecruiting(today))
    }
}
