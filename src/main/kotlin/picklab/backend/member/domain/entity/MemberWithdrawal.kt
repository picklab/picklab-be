package picklab.backend.member.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity
import picklab.backend.member.domain.enum.WithdrawalType

@Entity
@Table(name = "member_withdrawal")
class MemberWithdrawal(
    @Column(name = "member_id")
    @Comment("회원 ID")
    var memberId: Long,
    @Column(name = "withdrawal_reason", length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("탈퇴 사유")
    val withdrawalReason: WithdrawalType,
    @Column(name = "withdrawal_reason_detail", length = 2000)
    @Comment("탈퇴 상세 사유")
    val withdrawalReasonDetail: String? = null,
) : BaseEntity()
