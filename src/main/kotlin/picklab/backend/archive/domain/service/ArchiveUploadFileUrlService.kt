package picklab.backend.archive.domain.service

import org.springframework.stereotype.Service
import picklab.backend.archive.domain.entity.ArchiveUploadFileUrl
import picklab.backend.archive.domain.repository.ArchiveUploadFileUrlRepository

@Service
class ArchiveUploadFileUrlService(
    private val archiveUploadFileUrlRepository: ArchiveUploadFileUrlRepository,
) {
    fun saveAll(uploadedFileUrls: Collection<ArchiveUploadFileUrl>) {
        archiveUploadFileUrlRepository.saveAll(uploadedFileUrls)
    }
}