package picklab.backend.archive.domain.repository

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.archive.domain.entity.Archive
import picklab.backend.archive.domain.enums.ProgressStatus
import picklab.backend.member.domain.entity.Member

@Repository
interface ArchiveRepository : JpaRepository<Archive, Long> {
    fun findByIdAndMember(
        id: Long,
        member: Member,
    ): Archive?

    @Query("SELECT a FROM Archive a JOIN FETCH a.activity WHERE a.member = :member AND a.activityProgressStatus = :status")
    fun findByMemberAndProgressStatus(
        @Param("member") member: Member,
        @Param("status") status: ProgressStatus,
        sort: Sort,
    ): List<Archive>

    @Query(
        "SELECT a FROM Archive a JOIN FETCH a.activity WHERE a.member = :member AND a.activityProgressStatus = :status AND a.activityType = :activityType",
    )
    fun findByMemberAndProgressStatusAndActivityType(
        @Param("member") member: Member,
        @Param("status") status: ProgressStatus,
        @Param("activityType") activityType: ActivityType,
        sort: Sort,
    ): List<Archive>
}
