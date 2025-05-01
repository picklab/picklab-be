package picklab.backend.common.model

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성 시간")
    val createdAt: LocalDateTime = LocalDateTime.MIN,

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 시간")
    val updatedAt: LocalDateTime = LocalDateTime.MIN
)