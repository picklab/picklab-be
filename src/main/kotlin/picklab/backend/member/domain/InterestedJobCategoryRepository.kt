package picklab.backend.member.domain

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.member.domain.entity.InterestedJobCategory
import picklab.backend.member.domain.entity.Member

interface InterestedJobCategoryRepository : JpaRepository<InterestedJobCategory, Long> {
    fun deleteAllByMember(member: Member)
}
