package picklab.backend.notification

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.delete
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.notification.domain.entity.Notification
import picklab.backend.notification.domain.entity.NotificationType
import picklab.backend.notification.domain.repository.NotificationRepository
import picklab.backend.template.IntegrationTest

class NotificationIntegrationTest : IntegrationTest() {
    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var notificationRepository: NotificationRepository

    lateinit var member: Member

    @BeforeEach
    fun setUp() {
        cleanUp.all()

        member =
            memberRepository.save(
                Member(
                    name = "테스트 유저",
                    email = "test@example.com",
                ),
            )
    }

    @Nested
    @WithMockUser
    @DisplayName("알림 삭제 테스트")
    inner class DeleteNotificationTests {
        @Test
        @DisplayName("[성공] 내 알림을 개별 삭제한다")
        fun deleteNotificationSuccess() {
            // given
            val notification =
                notificationRepository.saveAndFlush(
                    Notification(
                        title = "테스트 알림",
                        type = NotificationType.ACTIVITY_CREATED,
                        link = "/activities/1",
                        member = member,
                    ),
                )

            // when
            mockMvc
                .delete("/notifications/${notification.id}")
                .andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.DELETE_NOTIFICATION_SUCCESS.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.DELETE_NOTIFICATION_SUCCESS.message) } }

            // then
            val deletedNotification = notificationRepository.findByIdIgnoreDelete(notification.id)
            assertThat(deletedNotification).isNotNull
            assertThat(deletedNotification!!.isRead).isTrue
            assertThat(deletedNotification.deletedAt).isNotNull
            assertThat(notificationRepository.findById(notification.id)).isEmpty
        }

        @Test
        @DisplayName("[실패] 다른 사용자의 알림은 삭제할 수 없다")
        fun cannotDeleteOtherMemberNotification() {
            // given
            val otherMember =
                memberRepository.save(
                    Member(
                        name = "다른 유저",
                        email = "other@example.com",
                    ),
                )
            val notification =
                notificationRepository.saveAndFlush(
                    Notification(
                        title = "다른 유저 알림",
                        type = NotificationType.ACTIVITY_CREATED,
                        link = "/activities/1",
                        member = otherMember,
                    ),
                )

            // when & then
            mockMvc
                .delete("/notifications/${notification.id}")
                .andExpect { status { isNotFound() } }
                .andExpect { jsonPath("$.code") { value(ErrorCode.NOTIFICATION_NOT_FOUND.status.value()) } }
                .andExpect { jsonPath("$.message") { value(ErrorCode.NOTIFICATION_NOT_FOUND.message) } }

            val notDeletedNotification = notificationRepository.findById(notification.id)
            assertThat(notDeletedNotification).isPresent
            assertThat(notDeletedNotification.get().deletedAt).isNull()
        }
    }
}
