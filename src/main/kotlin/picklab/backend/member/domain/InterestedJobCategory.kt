package picklab.backend.member.domain

import jakarta.persistence.*
import picklab.backend.common.model.BaseEntity
import picklab.backend.job.domain.JobCategory

@Entity
@Table(name = "member_interest_job_category")
class InterestedJobCategory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_category_id", nullable = false)
    val jobCategory: JobCategory,
) : BaseEntity()
