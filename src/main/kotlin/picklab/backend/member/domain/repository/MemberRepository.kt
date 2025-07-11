package picklab.backend.member.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import picklab.backend.member.domain.entity.Member
import java.util.Optional

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByIdAndDeletedAtIsNull(memberId: Long): Optional<Member>

    fun existsByIdAndDeletedAtIsNull(memberId: Long): Boolean

    fun existsByNicknameAndDeletedAtIsNull(nickname: String): Boolean

    @Query(nativeQuery = true, value = "select * from member where id = :memberId")
    fun findByIdIgnoreDelete(memberId: Long): Member?
}
