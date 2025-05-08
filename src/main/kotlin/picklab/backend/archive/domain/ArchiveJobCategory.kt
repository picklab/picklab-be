package picklab.backend.archive.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import picklab.backend.common.model.BaseEntity
import picklab.backend.job.domain.JobCategory

@Entity
@Table(name = "archive_job_category")
class ArchiveJobCategory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id", nullable = false)
    val archive: Archive,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_category_id", nullable = false)
    val jobCategory: JobCategory,
) : BaseEntity()
