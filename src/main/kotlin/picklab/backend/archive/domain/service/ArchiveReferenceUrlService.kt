package picklab.backend.archive.domain.service

import org.springframework.stereotype.Service
import picklab.backend.archive.domain.entity.ArchiveReferenceUrl
import picklab.backend.archive.domain.repository.ArchiveReferenceUrlRepository

@Service
class ArchiveReferenceUrlService(
    private val archiveReferenceUrlRepository: ArchiveReferenceUrlRepository,
) {
    fun saveAll(referenceUrls: Collection<ArchiveReferenceUrl>) {
        archiveReferenceUrlRepository.saveAll(referenceUrls)
    }
}
