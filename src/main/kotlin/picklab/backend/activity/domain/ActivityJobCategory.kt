package picklab.backend.activity.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import picklab.backend.common.model.BaseEntity
import picklab.backend.job.domain.JobCategory

@Entity
@Table(name = "activity_job_category")
class ActivityJobCategory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_category_id", nullable = false)
    val jobCategory: JobCategory,
) : BaseEntity()
