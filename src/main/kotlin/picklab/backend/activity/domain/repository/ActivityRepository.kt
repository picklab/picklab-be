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
    @Query(
        """
        SELECT a FROM Activity a 
        WHERE a.recruitmentEndDate = :targetDate 
        AND a.status = :status 
        ORDER BY a.id ASC
    """,
    )
    fun findByRecruitmentEndDateAndStatus(
        @Param("targetDate") targetDate: LocalDate,
        @Param("status") status: RecruitmentStatus,
    ): List<Activity>

    /**
     * 현재 모집 중인 활동 중 인기도가 가장 높은 활동을 조회합니다.
     * 인기도는 조회수와 북마크 수를 합산하여 계산합니다.
     */
    @Query(
        """
        SELECT a FROM Activity a 
        LEFT JOIN ActivityBookmark ab ON ab.activity.id = a.id 
        WHERE a.status = 'OPEN' 
        AND a.recruitmentEndDate >= CURRENT_DATE 
        GROUP BY a.id 
        ORDER BY (a.viewCount + COALESCE(COUNT(ab.id), 0)) DESC 
        LIMIT 1
    """,
    )
    fun findMostPopularActivity(): Activity?
}
