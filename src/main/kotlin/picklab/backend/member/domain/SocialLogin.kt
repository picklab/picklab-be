package picklab.backend.member.domain

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "social_login")
class SocialLogin(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("소셜 로그인 타입")
    val socialType: SocialType,

    @Column(nullable = false, length = 100)
    @Comment("소셜 로그인 ID")
    val socialId: String
) : BaseEntity()