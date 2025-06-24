package picklab.backend.archive.entrypoint.request

import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.archive.domain.entity.Archive
import picklab.backend.archive.domain.enums.DetailRoleType
import picklab.backend.archive.domain.enums.ProgressStatus
import picklab.backend.archive.domain.enums.RoleType
import picklab.backend.archive.domain.enums.WriteStatus
import picklab.backend.member.domain.entity.Member
import java.time.LocalDate

class ArchiveCreateRequest(
    val activityId: Long,
    val detailRole: DetailRoleType,
    val activityRecord: String,
    val activityType: ActivityType,
    val fileUrls: List<String>,
    val referenceUrls: List<String>,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val role: RoleType,
    val customRole: String?,
) {
    fun toCreateEntity(member: Member, activity: Activity): Archive = Archive(
        member = member,
        activity = activity,
        detailRole = detailRole,
        activityType = activityType,
        activityRecord = activityRecord,
        userStartDate = startDate,
        userEndDate = endDate,
        role = role,
        activityProgressStatus = ProgressStatus.IN_PROGRESSING,
        writeStatus = WriteStatus.IN_PROGRESS,
        customRole = customRole,
    )
}