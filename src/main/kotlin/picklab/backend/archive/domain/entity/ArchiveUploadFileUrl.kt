package picklab.backend.archive.domain.entity

import jakarta.persistence.*
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "archive_upload_file_url")
class ArchiveUploadFileUrl(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id", nullable = false)
    val archive: Archive,
    @Column(name = "url", nullable = false, length = 2000)
    val url: String,
) : BaseEntity()
