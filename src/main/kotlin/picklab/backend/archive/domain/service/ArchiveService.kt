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
            .findByIdAndMember(archiveId, member) ?: throw BusinessException(ErrorCode.NOT_FOUND_ARCHIVE)
}
