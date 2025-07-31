package picklab.backend.member.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import picklab.backend.member.domain.entity.InterestedJobCategory
import picklab.backend.member.domain.entity.Member

interface InterestedJobCategoryRepository : JpaRepository<InterestedJobCategory, Long> {
    fun deleteAllByMember(member: Member)

    @Query("SELECT jc.id FROM InterestedJobCategory ijc JOIN ijc.jobCategory jc WHERE ijc.member.id = :memberId")
    fun findJobCategoryIdsByMemberId(
        @Param("memberId") memberId: Long,
    ): List<Long>
}
