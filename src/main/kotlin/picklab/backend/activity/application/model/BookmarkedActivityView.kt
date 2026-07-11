package picklab.backend.activity.application.model

import java.time.LocalDate
import java.time.LocalDateTime

interface BookmarkedActivityView : ActivityView {
    val recruitmentStartDate: LocalDate
    val bookmarkedAt: LocalDateTime
}
