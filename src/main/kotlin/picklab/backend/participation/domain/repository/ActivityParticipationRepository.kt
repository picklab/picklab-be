package picklab.backend.participation.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.participation.domain.entity.ActivityParticipation
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus

interface ActivityParticipationRepository : JpaRepository<ActivityParticipation, Long> {
    fun findByMemberIdAndActivityId(
        memberId: Long,
        activityId: Long,
    ): ActivityParticipation?

    fun findAllByMemberId(
        memberId: Long,
        pageable: Pageable,
    ): Page<ActivityParticipation>

    fun findAllByMemberIdAndApplicationStatusIn(
        memberId: Long,
        applicationStatuses: List<ApplicationStatus>,
        pageable: Pageable,
    ): Page<ActivityParticipation>

    fun countByMemberId(memberId: Long): Long

    fun countByMemberIdAndApplicationStatus(
        memberId: Long,
        applicationStatus: ApplicationStatus,
    ): Long

    fun countByMemberIdAndProgressStatus(
        memberId: Long,
        progressStatus: ProgressStatus,
    ): Long
}
