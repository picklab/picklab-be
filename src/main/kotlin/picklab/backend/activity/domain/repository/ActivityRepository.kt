package picklab.backend.activity.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import picklab.backend.activity.domain.entity.Activity

@Repository
interface ActivityRepository : JpaRepository<Activity, Long> {
}