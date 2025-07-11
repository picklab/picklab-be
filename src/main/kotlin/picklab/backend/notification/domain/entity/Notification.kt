package picklab.backend.notification.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import picklab.backend.common.model.SoftDeleteEntity
import picklab.backend.member.domain.entity.Member

@Entity
@Table(name = "notification")
@SQLDelete(sql = "UPDATE notification SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class Notification(
    @Column(name = "title", nullable = false)
    @Comment("알림 제목")
    val title: String,
    @Column(name = "type", length = 50, nullable = false)
    @Comment("알림 타입")
    @Enumerated(EnumType.STRING)
    val type: NotificationType,
    @Column(name = "link", nullable = false)
    @Comment("클릭 시 이동할 링크")
    val link: String,
    @Column(name = "is_read", nullable = false)
    @Comment("읽음 여부")
    var isRead: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
) : SoftDeleteEntity() {

    fun read() {
        isRead = true
    }
}

enum class NotificationType {
    ACTIVITY_CREATED,
}
