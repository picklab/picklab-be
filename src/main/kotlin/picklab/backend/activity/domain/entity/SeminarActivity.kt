package picklab.backend.activity.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.hibernate.annotations.Comment
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentEndType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activitygroup.domain.entity.ActivityGroup
import java.time.LocalDate

@Entity
@DiscriminatorValue("SEMINAR")
class SeminarActivity(
    title: String,
    organizer: OrganizerType,
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
    activityGroup: ActivityGroup,
) : Activity(
        title = title,
        organizer = organizer,
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
