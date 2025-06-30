package picklab.backend.activity.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.activity.domain.entity.ActivityGroup

interface ActivityGroupRepository : JpaRepository<ActivityGroup, Long>
