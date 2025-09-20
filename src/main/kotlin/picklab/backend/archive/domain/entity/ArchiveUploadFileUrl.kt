package picklab.backend.archive.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
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
