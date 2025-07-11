package picklab.backend.participation.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.common.model.SoftDeleteEntity
import picklab.backend.member.domain.entity.Member
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus

@Entity
@Table(name = "activity_participation")
@SQLDelete(sql = "UPDATE activity_participation SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class ActivityParticipation(
    @Column(name = "application_status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("지원 상태 (지원 완료 / 최종 합격 / 불합격)")
    var applicationStatus: ApplicationStatus,
    @Column(name = "progress_status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("진행 상태 (진행 중 / 수료 완료 / 중도 포기)")
    var progressStatus: ProgressStatus,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity,
) : SoftDeleteEntity() {
    fun canWriteReview(): Boolean =
        progressStatus == ProgressStatus.COMPLETED ||
            progressStatus == ProgressStatus.DROPPED

    fun canArchive(): Boolean = progressStatus == ProgressStatus.COMPLETED
}
