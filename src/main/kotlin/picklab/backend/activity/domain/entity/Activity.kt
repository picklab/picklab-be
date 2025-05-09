package picklab.backend.activity.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.activity.domain.enum.ActivityFieldType
import picklab.backend.activity.domain.enum.ActivityType
import picklab.backend.activity.domain.enum.DomainType
import picklab.backend.activity.domain.enum.LocationType
import picklab.backend.activity.domain.enum.OrganizerType
import picklab.backend.activity.domain.enum.ParticipantType
import picklab.backend.activity.domain.enum.RecruitmentStatus
import picklab.backend.common.model.SoftDeleteEntity
import java.time.LocalDate

@Entity
@Table(name = "activity")
class Activity(
    @Column(name = "title", nullable = false, length = 50)
    @Comment("활동명")
    var title: String,
    @Column(name = "activity_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("활동 유형 (대외활동, 공모전/해커톤, 강연/세미나, 교육)")
    val activityType: ActivityType,
    @Column(name = "organizer", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("주최 기관/단체명")
    var organizer: OrganizerType,
    @Column(name = "target_audience", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("참여대상")
    var targetAudience: ParticipantType,
    @Column(name = "location", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("모임지역")
    var location: LocationType,
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
    @Column(name = "activity_field")
    @Enumerated(EnumType.STRING)
    @Comment("활동 분야(대외활동)")
    var activityField: ActivityFieldType,
    @Column(name = "domain")
    @Enumerated(EnumType.STRING)
    @Comment("도메인(공모전/해커톤)")
    var domain: DomainType,
    @Column(name = "cost", nullable = false)
    @Comment("시상 규모(공모전/해커톤), 교육비용(교육)")
    var cost: Long = 0, // 0: 없음
    @Column(name = "duration", nullable = false)
    @Comment("활동 기간(일)")
    var duration: Int? = -1, // -1: 무기한
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val activityGroup: ActivityGroup,
) : SoftDeleteEntity()
