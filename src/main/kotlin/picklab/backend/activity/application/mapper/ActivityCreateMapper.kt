package picklab.backend.activity.application.mapper

import picklab.backend.activity.application.model.ActivityCreateCommand
import picklab.backend.activity.application.model.CompetitionActivityCreateCommand
import picklab.backend.activity.application.model.EducationActivityCreateCommand
import picklab.backend.activity.application.model.ExternalActivityCreateCommand
import picklab.backend.activity.application.model.SeminarActivityCreateCommand
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.ActivityGroup
import picklab.backend.activity.domain.entity.ActivityJobCategory
import picklab.backend.activity.domain.entity.ActivityUploadFile
import picklab.backend.activity.domain.entity.CompetitionActivity
import picklab.backend.activity.domain.entity.EducationActivity
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.entity.SeminarActivity
import picklab.backend.job.domain.entity.JobCategory

fun ActivityCreateCommand.toEntity(activityGroup: ActivityGroup): Activity =
    when (this) {
        is ExternalActivityCreateCommand ->
            ExternalActivity(
                title = title,
                organizer = organizer,
                targetAudience = targetAudience,
                location = location,
                recruitmentStartDate = recruitmentStartDate,
                recruitmentEndDate = recruitmentEndDate,
                startDate = startDate,
                endDate = endDate,
                status = status,
                viewCount = 0L,
                duration = duration,
                activityHomepageUrl = activityHomepageUrl,
                activityApplicationUrl = activityApplicationUrl,
                activityThumbnailUrl = activityThumbnailUrl,
                description = description,
                benefit = benefit,
                activityGroup = activityGroup,
                activityField = activityField,
            )

        is SeminarActivityCreateCommand ->
            SeminarActivity(
                title = title,
                organizer = organizer,
                targetAudience = targetAudience,
                location = location,
                recruitmentStartDate = recruitmentStartDate,
                recruitmentEndDate = recruitmentEndDate,
                startDate = startDate,
                endDate = endDate,
                status = status,
                viewCount = 0L,
                duration = duration,
                activityHomepageUrl = activityHomepageUrl,
                activityApplicationUrl = activityApplicationUrl,
                activityThumbnailUrl = activityThumbnailUrl,
                description = description,
                benefit = benefit,
                activityGroup = activityGroup,
            )

        is EducationActivityCreateCommand ->
            EducationActivity(
                title = title,
                organizer = organizer,
                targetAudience = targetAudience,
                location = location,
                recruitmentStartDate = recruitmentStartDate,
                recruitmentEndDate = recruitmentEndDate,
                startDate = startDate,
                endDate = endDate,
                status = status,
                viewCount = 0L,
                duration = duration,
                activityHomepageUrl = activityHomepageUrl,
                activityApplicationUrl = activityApplicationUrl,
                activityThumbnailUrl = activityThumbnailUrl,
                description = description,
                benefit = benefit,
                cost = cost,
                costType = costType,
                format = educationFormat,
                activityGroup = activityGroup,
            )

        is CompetitionActivityCreateCommand ->
            CompetitionActivity(
                title = title,
                organizer = organizer,
                targetAudience = targetAudience,
                recruitmentStartDate = recruitmentStartDate,
                recruitmentEndDate = recruitmentEndDate,
                startDate = startDate,
                endDate = endDate,
                status = status,
                viewCount = 0L,
                duration = duration,
                activityHomepageUrl = activityHomepageUrl,
                activityApplicationUrl = activityApplicationUrl,
                activityThumbnailUrl = activityThumbnailUrl,
                description = description,
                benefit = benefit,
                activityGroup = activityGroup,
                domain = domain,
                cost = cost,
            )
    }

fun List<JobCategory>.toEntities(activity: Activity): List<ActivityJobCategory> =
    map { jobCategory ->
        ActivityJobCategory(
            activity = activity,
            jobCategory = jobCategory,
        )
    }

fun ActivityCreateCommand.toUploadFileEntities(activity: Activity): List<ActivityUploadFile> =
    uploadFiles.map { uploadFile ->
        ActivityUploadFile(
            name = uploadFile.name,
            url = uploadFile.url,
            activity = activity,
        )
    }
