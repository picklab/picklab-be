package picklab.backend.participation.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.participation.domain.entity.ActivityParticipation

interface ActivityParticipationRepository : JpaRepository<ActivityParticipation, Long> {
    fun findByMemberIdAndActivityId(
        memberId: Long,
        activityId: Long,
    ): ActivityParticipation?
}
