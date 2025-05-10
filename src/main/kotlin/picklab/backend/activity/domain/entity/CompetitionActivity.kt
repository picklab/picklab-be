package picklab.backend.activity.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.hibernate.annotations.Comment
import picklab.backend.activity.domain.enum.DomainType
import picklab.backend.activity.domain.enum.OrganizerType
import picklab.backend.activity.domain.enum.ParticipantType
import picklab.backend.activity.domain.enum.RecruitmentStatus
import java.time.LocalDate

@Entity
@DiscriminatorValue("COMPETITION")
class CompetitionActivity(
    title: String,
    organizer: OrganizerType,
    targetAudience: ParticipantType,
    recruitmentStartDate: LocalDate,
    recruitmentEndDate: LocalDate,
    startDate: LocalDate,
    endDate: LocalDate,
    status: RecruitmentStatus,
    viewCount: Long = 0L,
    duration: Int? = -1,
    activityGroup: ActivityGroup,
    @Column(name = "domain")
    @Enumerated(EnumType.STRING)
    @Comment("도메인")
    var domain: DomainType,
    @Column(name = "cost")
    @Comment("시상 규모")
    var cost: Long = 0, // 0은 없음을 의미
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
