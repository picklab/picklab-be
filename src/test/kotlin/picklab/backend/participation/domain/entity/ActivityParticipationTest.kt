package picklab.backend.participation.domain.entity

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.ActivityGroup
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.member.domain.entity.Member
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus
import java.time.LocalDate
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActivityParticipationTest {
    private lateinit var mockMember: Member
    private lateinit var mockActivity: Activity

    @BeforeEach
    fun setup() {
        mockMember =
            Member(
                name = "홍길동",
                email = "test@example.com",
            )
        mockMember
            .javaClass
            .superclass
            .superclass
            .getDeclaredField("id")
            .apply { isAccessible = true }
            .set(mockMember, 1L)

        val fixedDate = LocalDate.of(2025, 1, 1)
        mockActivity =
            object : Activity(
                title = "테스트 활동",
                organizer = OrganizerType.ETC,
                targetAudience = ParticipantType.ALL,
                recruitmentStartDate = fixedDate,
                recruitmentEndDate = fixedDate,
                startDate = fixedDate,
                endDate = fixedDate,
                status = RecruitmentStatus.OPEN,
                activityGroup =
                    ActivityGroup(
                        name = "테스트 그룹",
                        description = "의미 없음",
                    ),
            ) {}
        mockActivity
            .javaClass
            .superclass
            .superclass
            .superclass
            .getDeclaredField("id")
            .apply { isAccessible = true }
            .set(mockActivity, 1L)
    }

    @Test
    @DisplayName("진행 중일 때는 리뷰 및 아카이브 작성이 불가능하다")
    fun cannotWriteInProgressing() {
        // given
        val participation =
            ActivityParticipation(
                applicationStatus = ApplicationStatus.APPLIED,
                progressStatus = ProgressStatus.IN_PROGRESSING,
                member = mockMember,
                activity = mockActivity,
            )

        // when & then
        assertFalse(participation.canWriteReview())
        assertFalse(participation.canArchive())
    }

    @Test
    @DisplayName("수료 완료 상태는 리뷰와 아카이브 작성이 가능하다")
    fun canWriteCompleted() {
        // given
        val participation =
            ActivityParticipation(
                applicationStatus = ApplicationStatus.APPLIED,
                progressStatus = ProgressStatus.COMPLETED,
                member = mockMember,
                activity = mockActivity,
            )

        // when & then
        assertTrue(participation.canWriteReview())
        assertTrue(participation.canArchive())
    }

    @Test
    @DisplayName("중도 하차 상태는 리뷰만 가능하고 아카이브는 불가능하다")
    fun onlyReviewDropped() {
        // given
        val participation =
            ActivityParticipation(
                applicationStatus = ApplicationStatus.APPLIED,
                progressStatus = ProgressStatus.DROPPED,
                member = mockMember,
                activity = mockActivity,
            )

        // when & then
        assertTrue(participation.canWriteReview())
        assertFalse(participation.canArchive())
    }
}
