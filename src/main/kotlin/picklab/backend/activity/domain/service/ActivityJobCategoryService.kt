package picklab.backend.activity.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.entity.ActivityJobCategory
import picklab.backend.activity.domain.repository.ActivityJobCategoryRepository

@Service
class ActivityJobCategoryService(
    private val activityJobCategoryRepository: ActivityJobCategoryRepository,
) {
    @Transactional
    fun saveAll(entities: List<ActivityJobCategory>) {
        activityJobCategoryRepository.saveAll(entities)
    }
}
