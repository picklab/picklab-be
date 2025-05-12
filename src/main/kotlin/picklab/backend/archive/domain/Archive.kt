package picklab.backend.archive.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.enum.ActivityType
import picklab.backend.common.model.SoftDeleteEntity
import picklab.backend.job.domain.JobDetail
import picklab.backend.job.domain.JobGroup
import picklab.backend.member.domain.Member
import java.time.LocalDateTime

@Entity
@Table(name = "archive")
class Archive(
    @Column(name = "user_start_date", nullable = false)
    @Comment("활동 시작일")
    var userStartDate: LocalDateTime,
    @Column(name = "user_end_date", nullable = false)
    @Comment("활동 종료일")
    var userEndDate: LocalDateTime,
    @Column(name = "role", length = 50, nullable = false)
    @Comment("활동 역할")
    var role: JobGroup,
    @Column(name = "detail_role", length = 50, nullable = false)
    @Comment("상세 역할")
    var detailRole: JobDetail,
    @Column(name = "custom_role", length = 255)
    @Comment("상세 역할에서 기타를 선택하여 직접 입력한 역할")
    var customRole: String? = null,
    @Lob
    @Column(name = "activity_record", nullable = false, columnDefinition = "TEXT")
    @Comment("활동 기록")
    var activityRecord: String,
    @Column(name = "uploaded_file_url", length = 255)
    @Comment("업로드한 파일 URL")
    var uploadedFileUrl: String? = null,
    @Column(name = "reference_url", length = 255)
    @Comment("참고 URL")
    var referenceUrl: String? = null,
    @Column(name = "activity_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("활동 구분")
    var activityType: ActivityType,
    @Column(name = "activity_progress_status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("활동 진행 상태")
    var activityProgressStatus: ProgressStatus,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity,
    @OneToMany(mappedBy = "archive", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    val archiveJobCategory: MutableList<ArchiveJobCategory> = mutableListOf(),
) : SoftDeleteEntity()
