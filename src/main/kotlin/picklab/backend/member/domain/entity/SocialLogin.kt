package picklab.backend.member.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity
import picklab.backend.member.domain.enums.SocialType

@Entity
@Table(name = "social_login")
class SocialLogin(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(name = "social_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("소셜 로그인 타입")
    val socialType: SocialType,
    @Column(name = "social_id", nullable = false, length = 100)
    @Comment("소셜 로그인 ID")
    val socialId: String,
) : BaseEntity()
