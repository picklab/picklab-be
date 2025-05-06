package picklab.backend.activity.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.common.model.SoftDeleteEntity
import java.time.LocalDateTime

@Entity
@Table(name = "activity")
class Activity(
    @Column(name = "title", nullable = false, length = 50)
    @Comment("활동명")
    var title: String = "",
    @Column(name = "post_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("활동 유형 (대외활동, 공모전/해커톤, 세미나, 교육)")
    val postType: PostType,
    @Column(name = "organizer", nullable = false, length = 50)
    @Comment("주최 기관/단체명")
    var organizer: String = "",
    @Column(name = "target_audience", nullable = false, length = 50)
    @Comment("참여대상")
    var targetAudience: String = "",
    @Column(name = "location", nullable = false, length = 50)
    @Comment("모임지역")
    var location: String = "",
    @Column(name = "recruitment_start_date", nullable = false)
    @Comment("모집 시작일")
    var recruitmentStartDate: LocalDateTime = LocalDateTime.MIN,
    @Column(name = "recruitment_end_date", nullable = false)
    @Comment("모집 종료일")
    var recruitmentEndDate: LocalDateTime = LocalDateTime.MIN,
    @Column(name = "start_date", nullable = false)
    @Comment("활동 시작일")
    var startDate: LocalDateTime = LocalDateTime.MIN,
    @Column(name = "end_date", nullable = false)
    @Comment("활동 종료일")
    var endDate: LocalDateTime = LocalDateTime.MIN,
    @Column(name = "status", nullable = false)
    @Comment("모집 상태(모집 중, 모집 마감)")
    var status: ActivityStatus = ActivityStatus.CLOSED,
    @Column(name = "view_count", nullable = false)
    @Comment("조회수")
    var viewCount: Long = 0L,
    @Column(name = "activity_field", nullable = false)
    @Comment("대외활동 세부 분야")
    var activityField: String = "",
    @Column(name = "domain")
    @Comment("공모전/해커톤 세부 영역")
    var domain: String = "",
    @Column(name = "cost")
    @Comment("공모전(시상 규모), 교육(교육비용)")
    var cost: Long? = null,
    @Column(name = "duration")
    @Comment("활동 기간(일)")
    var duration: Int? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    val activitySeries: ActivitySeries,
) : SoftDeleteEntity()
