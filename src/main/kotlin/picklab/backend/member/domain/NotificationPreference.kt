package picklab.backend.member.domain

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "member_notification_preference")
class NotificationPreference(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(name = "notify_popular_activity", nullable = false)
    @Comment("인기 알림 수신 동의 여부")
    val notifyPopularActivity: Boolean,

    @Column(name = "notify_bookmarked_activity", nullable = false)
    @Comment("북마크 알림 수신 동의 여부")
    val notifyBookmarkedActivity: Boolean,
) : BaseEntity()