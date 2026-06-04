package picklab.backend.archive.domain.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import picklab.backend.archive.domain.entity.Archive
import picklab.backend.archive.domain.repository.ArchiveRepository
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.entity.Member

@Service
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
) {
    @Transactional
    fun save(entity: Archive): Archive = archiveRepository.save(entity)

    fun mustFindByIdAndMember(
        archiveId: Long,
        member: Member,
    ): Archive =
        archiveRepository
            .findByIdAndParticipationMember(archiveId, member) ?: throw BusinessException(ErrorCode.NOT_FOUND_ARCHIVE)

    fun findByParticipationId(participationId: Long): Archive? = archiveRepository.findByParticipationId(participationId)

    fun existsActiveByActivityIdAndMemberId(
        activityId: Long,
        memberId: Long,
    ): Boolean =
        archiveRepository.existsByParticipationActivityIdAndParticipationMemberIdAndDeletedAtIsNull(
            activityId,
            memberId,
        )

    fun existsActiveByParticipationId(participationId: Long): Boolean =
        archiveRepository.existsByParticipationIdAndDeletedAtIsNull(
            participationId,
        )

    fun findAllByParticipationIds(participationIds: Collection<Long>): List<Archive> =
        if (participationIds.isEmpty()) {
            emptyList()
        } else {
            archiveRepository.findAllByParticipationIdIn(participationIds)
        }
}
