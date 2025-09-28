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
@Table(name = "member_agreement")
class MemberAgreement(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(nullable = false)
    @Comment("이메일 수신 동의 여부")
    var emailAgreement: Boolean,
    @Column(nullable = false)
    @Comment("개인정보 수집 및 이용 동의 여부")
    var privacyAgreement: Boolean,
) : BaseEntity() {
    fun updateEmailAgreement(agree: Boolean) {
        this.emailAgreement = agree
    }
}
