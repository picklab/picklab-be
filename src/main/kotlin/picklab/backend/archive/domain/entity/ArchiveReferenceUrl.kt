package picklab.backend.archive.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "archive_reference_url")
class ArchiveReferenceUrl(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id", nullable = false)
    val archive: Archive,
    @Column(name = "url", nullable = false, length = 2000)
    @Comment("참고 URL")
    val url: String,
) : BaseEntity()
