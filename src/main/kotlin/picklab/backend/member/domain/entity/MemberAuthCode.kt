package picklab.backend.member.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "member_auth_code")
class MemberAuthCode(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(name = "code", nullable = false, length = 20)
    @Comment("인증 코드")
    val code: String,
) : BaseEntity()
