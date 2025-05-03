package picklab.backend.job.domain

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "job_category")
class JobCategory(

    @Column(name = "job_group", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("직무 대분류 (기획 / 디자인 / 개발 / 마케팅 등)")
    val jobGroup: JobGroup,

    @Column(name = "job_detail", length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("직무 세부 분류 (nullable)")
    val jobDetail: JobDetail? = null

) : BaseEntity()