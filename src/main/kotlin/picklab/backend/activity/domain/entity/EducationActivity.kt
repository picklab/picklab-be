package picklab.backend.activity.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.hibernate.annotations.Comment
import picklab.backend.activity.domain.enum.LocationType
import picklab.backend.activity.domain.enum.OrganizerType
import picklab.backend.activity.domain.enum.ParticipantType
import picklab.backend.activity.domain.enum.RecruitmentStatus
import java.time.LocalDate

@Entity
@DiscriminatorValue("EDUCATION")
class EducationActivity(
    title: String,
    organizer: OrganizerType,
    targetAudience: ParticipantType,
    @Column(name = "location", length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("모임지역")
    var location: LocationType,
    recruitmentStartDate: LocalDate,
    recruitmentEndDate: LocalDate,
    startDate: LocalDate,
    endDate: LocalDate,
    status: RecruitmentStatus,
    viewCount: Long,
    duration: Int,
    activityGroup: ActivityGroup,
    @Column(name = "cost")
    @Comment("교육비용(교육)")
    var cost: Long,
) : Activity(
        title = title,
        organizer = organizer,
        targetAudience = targetAudience,
        recruitmentStartDate = recruitmentStartDate,
        recruitmentEndDate = recruitmentEndDate,
        startDate = startDate,
        endDate = endDate,
        status = status,
        viewCount = viewCount,
        duration = duration,
        activityGroup = activityGroup,
    )
