package picklab.backend.member.entrypoint.mapper

import picklab.backend.member.application.model.MemberMeResult
import picklab.backend.member.entrypoint.response.EmploymentInfoResponse
import picklab.backend.member.entrypoint.response.GetMemberMeResponse

fun MemberMeResult.toResponse(): GetMemberMeResponse =
    GetMemberMeResponse(
        name = this.name,
        nickname = this.nickname,
        educationLevel = this.educationLevel,
        birthDate = this.birthDate,
        selectedInterestedJobs = this.selectedInterestedJobs,
        jobFields = this.jobFields,
        employment =
            EmploymentInfoResponse(
                employmentStatus = this.employmentStatus,
                company = this.company,
            ),
    )
