package picklab.backend.activity.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.activity.domain.entity.ActivityJobCategory

interface ActivityJobCategoryRepository : JpaRepository<ActivityJobCategory, Long>
