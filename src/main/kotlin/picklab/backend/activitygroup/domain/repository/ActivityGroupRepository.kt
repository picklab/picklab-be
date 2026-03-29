package picklab.backend.activitygroup.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.activitygroup.domain.entity.ActivityGroup

interface ActivityGroupRepository : JpaRepository<ActivityGroup, Long>
