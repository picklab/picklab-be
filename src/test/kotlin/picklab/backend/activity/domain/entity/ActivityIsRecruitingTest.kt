package picklab.backend.activity.domain.entity

import org.junit.jupiter.api.DisplayName
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentEndType
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
        recruitmentEndType: RecruitmentEndType,
        status: RecruitmentStatus = RecruitmentStatus.OPEN,
    ) = ExternalActivity(
        title = "테스트 활동",
        organizerType = OrganizerType.LARGE_CORPORATION,
        targetAudience = ParticipantType.ALL,
        location = LocationType.ALL,
        recruitmentStartDate = LocalDate.of(2026, 1, 1),
        recruitmentEndDate = recruitmentEndDate,
        recruitmentEndType = recruitmentEndType,
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
    @DisplayName("FIXED: 마감일이 오늘 이후면 모집 중")
    fun fixedBeforeDeadlineIsRecruiting() {
        assertTrue(activity(today.plusDays(1), RecruitmentEndType.FIXED).isRecruiting(today))
    }

    @Test
    @DisplayName("FIXED: 마감일이 오늘이면 모집 중")
    fun fixedOnDeadlineIsRecruiting() {
        assertTrue(activity(today, RecruitmentEndType.FIXED).isRecruiting(today))
    }

    @Test
    @DisplayName("FIXED: 마감일이 지나면 모집 종료")
    fun fixedAfterDeadlineIsNotRecruiting() {
        assertFalse(activity(today.minusDays(1), RecruitmentEndType.FIXED).isRecruiting(today))
    }

    @Test
    @DisplayName("ALWAYS_OPEN: status와 무관하게 항상 모집 중")
    fun alwaysOpenIsAlwaysRecruiting() {
        assertTrue(activity(null, RecruitmentEndType.ALWAYS_OPEN, RecruitmentStatus.CLOSED).isRecruiting(today))
    }

    @Test
    @DisplayName("CLOSE_ON_HIRE: status가 OPEN이면 모집 중")
    fun closeOnHireWithOpenStatusIsRecruiting() {
        assertTrue(activity(null, RecruitmentEndType.CLOSE_ON_HIRE, RecruitmentStatus.OPEN).isRecruiting(today))
    }

    @Test
    @DisplayName("CLOSE_ON_HIRE: status가 CLOSED이면 모집 종료")
    fun closeOnHireWithClosedStatusIsNotRecruiting() {
        assertFalse(activity(null, RecruitmentEndType.CLOSE_ON_HIRE, RecruitmentStatus.CLOSED).isRecruiting(today))
    }
}
