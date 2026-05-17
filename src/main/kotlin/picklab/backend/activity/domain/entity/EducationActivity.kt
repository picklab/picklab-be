package picklab.backend.activity.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.hibernate.annotations.Comment
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentEndType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activitygroup.domain.entity.ActivityGroup
import java.time.LocalDate

@Entity
@DiscriminatorValue("EDUCATION")
class EducationActivity(
    title: String,
    organizer: String? = null,
    organizerType: OrganizerType,
    targetAudience: ParticipantType,
    @Column(name = "location", length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("모임지역")
    var location: LocationType,
    recruitmentStartDate: LocalDate,
    recruitmentEndDate: LocalDate?,
    recruitmentEndType: RecruitmentEndType = RecruitmentEndType.FIXED,
    startDate: LocalDate,
    endDate: LocalDate?,
    status: RecruitmentStatus,
    viewCount: Long,
    duration: Int,
    activityHomepageUrl: String?,
    activityApplicationUrl: String?,
    activityThumbnailUrl: String?,
    description: String?,
    benefit: String,
    @Column(name = "cost")
    @Comment("교육비용(교육)")
    var cost: Long,
    @Column(name = "education_cost_type")
    @Enumerated(EnumType.STRING)
    @Comment("교육 비용 유형(무료, 유료, 전액 국비지원, 일부 국비지원")
    var costType: EducationCostType,
    @Column(name = "education_format", length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("교육 형식(온라인, 오프라인, 모두)")
    var format: EducationFormatType,
    activityGroup: ActivityGroup,
) : Activity(
        title = title,
        organizer = organizer,
        organizerType = organizerType,
        targetAudience = targetAudience,
        recruitmentStartDate = recruitmentStartDate,
        recruitmentEndDate = recruitmentEndDate,
        recruitmentEndType = recruitmentEndType,
        startDate = startDate,
        endDate = endDate,
        status = status,
        viewCount = viewCount,
        duration = duration,
        activityHomepageUrl = activityHomepageUrl,
        activityApplicationUrl = activityApplicationUrl,
        activityThumbnailUrl = activityThumbnailUrl,
        description = description,
        benefit = benefit,
        activityGroup = activityGroup,
    )
