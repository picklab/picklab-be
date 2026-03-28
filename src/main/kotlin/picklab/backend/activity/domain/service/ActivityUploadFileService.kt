package picklab.backend.activity.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.entity.ActivityUploadFile
import picklab.backend.activity.domain.repository.ActivityUploadFileRepository

@Service
class ActivityUploadFileService(
    private val activityUploadFileRepository: ActivityUploadFileRepository,
) {
    @Transactional
    fun saveAll(entities: List<ActivityUploadFile>) {
        activityUploadFileRepository.saveAll(entities)
    }
}
