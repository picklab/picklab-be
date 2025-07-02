package picklab.backend.activity.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.activity.domain.entity.Activity
import java.util.Optional

interface ActivityRepository :
    JpaRepository<Activity, Long>,
    ActivityRepositoryCustom {
    fun findByIdAndDeletedAtIsNull(activityId: Long): Optional<Activity>
}
