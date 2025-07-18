package picklab.backend.activity.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.common.model.SoftDeleteEntity
import java.time.LocalDate

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "activity_type",
    discriminatorType = DiscriminatorType.STRING,
)
@Table(name = "activity")
@SQLDelete(sql = "UPDATE activity SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
abstract class Activity(
    @Column(name = "activity_type", insertable = false, updatable = false)
    @Comment("활동 유형 (대외활동, 공모전/해커톤, 강연/세미나, 교육)")
    val activityType: String? = null,
    @Column(name = "title", nullable = false, length = 50)
    @Comment("활동명")
    var title: String,
    @Column(name = "organizer", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("주최 기관/단체명")
    var organizer: OrganizerType,
    @Column(name = "target_audience", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("참여대상")
    var targetAudience: ParticipantType,
    @Column(name = "recruitment_start_date", nullable = false)
    @Comment("모집 시작일")
    var recruitmentStartDate: LocalDate,
    @Column(name = "recruitment_end_date", nullable = false)
    @Comment("모집 종료일")
    var recruitmentEndDate: LocalDate,
    @Column(name = "start_date", nullable = false)
    @Comment("활동 시작일")
    var startDate: LocalDate,
    @Column(name = "end_date", nullable = false)
    @Comment("활동 종료일")
    var endDate: LocalDate,
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("모집 상태(모집 중, 모집 마감)")
    var status: RecruitmentStatus,
    @Column(name = "view_count", nullable = false)
    @Comment("조회수")
    var viewCount: Long = 0L,
    @Column(name = "duration", nullable = false)
    @Comment("활동 기간(일)")
    var duration: Int = UNLIMITED_DURATION,
    @Column(name = "activity_thumbnail_url")
    @Comment("활동 썸네일 이미지 URL")
    var activityThumbnailUrl: String? = null,
    @Column(name = "description", length = 2000)
    @Comment("활동 내용")
    var description: String = "",
    @Column(name = "benefit", length = 2000)
    @Comment("활동 혜택")
    var benefit: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val activityGroup: ActivityGroup,
) : SoftDeleteEntity() {
    companion object {
        const val UNLIMITED_DURATION = -1
    }

    fun increaseViewCount() {
        this.viewCount++
    }
}
