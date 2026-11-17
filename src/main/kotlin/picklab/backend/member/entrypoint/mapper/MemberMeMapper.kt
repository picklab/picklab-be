package picklab.backend.member.entrypoint.mapper

import picklab.backend.member.application.model.MemberMeResult
import picklab.backend.member.entrypoint.response.EmploymentInfoResponse
import picklab.backend.member.entrypoint.response.GetMemberMeResponse
import picklab.backend.member.entrypoint.response.NotificationPreferencesResponse

fun MemberMeResult.toResponse(): GetMemberMeResponse =
    GetMemberMeResponse(
        name = this.name,
        nickname = this.nickname,
        educationLevel = this.educationLevel,
        school = this.school,
        graduationStatus = this.graduationStatus,
        profileImage = this.profileImage,
        birthDate = this.birthDate,
        selectedInterestedJobs = this.selectedInterestedJobs,
        jobFields = this.jobFields,
        employment =
            EmploymentInfoResponse(
                employmentStatus = this.employmentStatus,
                company = this.company,
            ),
        emailAgreement = this.emailAgreement,
        notificationPreferences =
            NotificationPreferencesResponse(
                popular = this.notifyPopularActivity,
                bookmarked = this.notifyBookmarkedActivity,
            ),
    )
