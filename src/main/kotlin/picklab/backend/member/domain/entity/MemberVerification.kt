package picklab.backend.member.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import picklab.backend.common.model.SoftDeleteEntity
import java.time.LocalDateTime

@Entity
@Table(name = "member_verification")
@SQLDelete(sql = "UPDATE member_verification SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class MemberVerification(
    @Column(name = "email", length = 100, nullable = false)
    @Comment("인증 이메일")
    val email: String,
    @Column(name = "code", length = 10, nullable = false)
    @Comment("인증 코드")
    val code: String,
    @Column(name = "expired_at", nullable = false)
    @Comment("인증 만료 시간")
    val expiredAt: LocalDateTime,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
) : SoftDeleteEntity()
