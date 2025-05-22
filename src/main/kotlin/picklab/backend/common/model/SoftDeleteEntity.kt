package picklab.backend.common.model

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@MappedSuperclass
@SQLRestriction("deleted_at IS NULL")
abstract class SoftDeleteEntity(
    @Column(name = "deleted_at")
    @Comment("삭제 시간")
    var deletedAt: LocalDateTime? = null,
) : BaseEntity() {
    fun delete() {
        deletedAt = LocalDateTime.now()
    }

    fun isDeleted() = deletedAt != null
}
