package picklab.backend.activity.application.model

import picklab.backend.activity.domain.enums.RecruitmentEndType
import java.time.LocalDate

interface ActivityView {
    val id: Long
    val title: String
    val organization: String?
    val organizerType: String
    val startDate: LocalDate
    val category: String
    val jobTags: List<String>
    val thumbnailUrl: String?
    val viewCount: Long
    val recruitmentEndDate: LocalDate?
    val recruitmentEndType: RecruitmentEndType
}
