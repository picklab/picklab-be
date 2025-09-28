package picklab.backend.member.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
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
    var notifyPopularActivity: Boolean,
    @Column(name = "notify_bookmarked_activity", nullable = false)
    @Comment("북마크 알림 수신 동의 여부")
    var notifyBookmarkedActivity: Boolean,
) : BaseEntity() {
    fun togglePopular() {
        this.notifyPopularActivity = !this.notifyPopularActivity
    }

    fun toggleBookmarked() {
        this.notifyBookmarkedActivity = !this.notifyBookmarkedActivity
    }
}
