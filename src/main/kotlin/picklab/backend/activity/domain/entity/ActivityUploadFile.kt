package picklab.backend.activity.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "activity_upload_file")
class ActivityUploadFile(
    @Column(name = "name", nullable = false)
    @Comment("파일명(ex. 지원 PDF)")
    var name: String,
    @Column(name = "url", nullable = false, length = 2084)
    @Comment("파일 URL")
    var url: String,
) : BaseEntity()
