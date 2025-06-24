package picklab.backend.archive.domain.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import picklab.backend.archive.domain.entity.Archive
import picklab.backend.archive.domain.repository.ArchiveReferenceUrlRepository
import picklab.backend.archive.domain.repository.ArchiveRepository

@Service
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
) {

    @Transactional
    fun save(entity: Archive): Archive {
        return archiveRepository.save(entity)
    }
}