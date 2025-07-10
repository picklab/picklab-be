package picklab.backend.archive.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.archive.domain.enums.DetailRoleType
import picklab.backend.archive.domain.enums.PassOrFailStatus
import picklab.backend.archive.domain.enums.ProgressStatus
import picklab.backend.archive.domain.enums.RoleType
import picklab.backend.archive.domain.enums.WriteStatus
import picklab.backend.common.model.SoftDeleteEntity
import picklab.backend.member.domain.entity.Member
import java.time.LocalDate

@Entity
@Table(name = "archive")
@SQLDelete(sql = "UPDATE archive SET deleted_at = NOW() WHERE id = ?")
class Archive(
    @Column(name = "user_start_date", nullable = false)
    @Comment("활동 시작일")
    var userStartDate: LocalDate,
    @Column(name = "user_end_date", nullable = false)
    @Comment("활동 종료일")
    var userEndDate: LocalDate,
    @Column(name = "role", length = 50, nullable = false)
    @Comment("활동 역할")
    var role: RoleType,
    @Column(name = "detail_role", length = 50, nullable = false)
    @Comment("상세 역할")
    var detailRole: DetailRoleType,
    @Lob
    @Column(name = "activity_record", nullable = false, columnDefinition = "TEXT")
    @Comment("활동 기록")
    var activityRecord: String,
    @Column(name = "activity_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("활동 구분")
    var activityType: ActivityType,
    @Column(name = "activity_progress_status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("활동 진행 상태")
    var activityProgressStatus: ProgressStatus,
    @Column(name = "write_status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("작성 상태")
    var writeStatus: WriteStatus = WriteStatus.NOT_WRITTEN,
    @Column(name = "custom_role", length = 255)
    @Comment("상세 역할에서 기타를 선택하여 직접 입력한 역할")
    var customRole: String? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity,
    @Comment("합불 여부")
    @Enumerated(EnumType.STRING)
    @Column(name = "pass_or_fail_status", nullable = false)
    var passOrFailStatus: PassOrFailStatus = PassOrFailStatus.FAIL,
) : SoftDeleteEntity() {

    fun update(
        activityProgressStatus: ProgressStatus = this.activityProgressStatus,
        passOrFailStatus: PassOrFailStatus = this.passOrFailStatus,
    ) {
        this.activityProgressStatus = activityProgressStatus
        this.passOrFailStatus = passOrFailStatus
    }
}
