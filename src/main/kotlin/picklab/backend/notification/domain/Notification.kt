package picklab.backend.notification.domain

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import picklab.backend.common.model.SoftDeleteEntity
import picklab.backend.member.domain.Member

@Entity
@Table(name = "notification")
class Notification(
    @Column(name = "title", nullable = false)
    @Comment("알림 제목")
    val title: String,
    @Column(name = "type", length = 50, nullable = false)
    @Comment("알림 타입")
    val type: String,
    @Column(name = "link", nullable = false)
    @Comment("클릭 시 이동할 링크")
    val link: String,
    @Column(name = "is_read", nullable = false)
    @Comment("읽음 여부")
    var isRead: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
) : SoftDeleteEntity()
