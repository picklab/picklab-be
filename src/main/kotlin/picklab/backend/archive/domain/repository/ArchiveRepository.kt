package picklab.backend.archive.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import picklab.backend.archive.domain.entity.Archive
import picklab.backend.member.domain.entity.Member

@Repository
interface ArchiveRepository : JpaRepository<Archive, Long> {
    fun findByIdAndParticipationMember(
        id: Long,
        member: Member,
    ): Archive?

    fun findByParticipationId(participationId: Long): Archive?

    fun existsByParticipationActivityIdAndParticipationMemberIdAndDeletedAtIsNull(
        activityId: Long,
        memberId: Long,
    ): Boolean

    fun existsByParticipationIdAndDeletedAtIsNull(participationId: Long): Boolean

    fun findAllByParticipationIdIn(participationIds: Collection<Long>): List<Archive>
}
