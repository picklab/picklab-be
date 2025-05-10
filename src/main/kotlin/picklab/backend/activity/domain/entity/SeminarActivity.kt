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
    recruitmentEndDate: LocalDate,
    startDate: LocalDate,
    endDate: LocalDate,
    status: RecruitmentStatus,
    viewCount: Long = 0L,
    duration: Int? = -1,
    activityGroup: ActivityGroup,
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
