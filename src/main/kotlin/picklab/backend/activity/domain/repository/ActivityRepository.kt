package picklab.backend.activity.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.enums.RecruitmentStatus
import java.time.LocalDate

interface ActivityRepository :
    JpaRepository<Activity, Long>,
    ActivityRepositoryCustom {

    /**
     * 특정 모집 종료일에 해당하는 모집 중인 활동들을 조회합니다
     */
    @Query("""
        SELECT a FROM Activity a 
        WHERE a.recruitmentEndDate = :targetDate 
        AND a.status = :status 
        AND a.deletedAt IS NULL
        ORDER BY a.id ASC
    """)
    fun findByRecruitmentEndDateAndStatus(
        @Param("targetDate") targetDate: LocalDate,
        @Param("status") status: RecruitmentStatus
    ): List<Activity>
}
