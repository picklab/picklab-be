package picklab.backend.member.domain.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import picklab.backend.member.domain.entity.Member
import java.time.LocalDateTime
import java.util.Optional

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByIdAndDeletedAtIsNull(memberId: Long): Optional<Member>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Member m WHERE m.id = :memberId AND m.deletedAt IS NULL")
    fun findActiveMemberForUpdate(memberId: Long): Optional<Member>

    @Query(
        nativeQuery = true,
        value = "SELECT id, deleted_at AS deletedAt FROM member WHERE deleted_at > :since",
    )
    fun findWithdrawnSince(since: LocalDateTime): List<WithdrawnMember>

    fun existsByIdAndDeletedAtIsNull(memberId: Long): Boolean

    fun existsByNicknameAndDeletedAtIsNull(nickname: String): Boolean

    @Query(nativeQuery = true, value = "select * from member where id = :memberId")
    fun findByIdIgnoreDelete(memberId: Long): Member?

    interface WithdrawnMember {
        val id: Long
        val deletedAt: LocalDateTime
    }
}
