package picklab.backend.member.application.mapper

import picklab.backend.member.application.model.MemberMeResult
import picklab.backend.member.domain.entity.InterestedJobCategory
import picklab.backend.member.domain.entity.Member

fun Member.toMemberMeResult(interestedJobCategories: List<InterestedJobCategory>): MemberMeResult =
    MemberMeResult(
        name = this.name,
        nickname = this.nickname,
        educationLevel = this.educationLevel,
        birthDate = this.birthDate,
        selectedInterestedJobs = interestedJobCategories.mapNotNull { it.jobCategory.jobDetail },
        jobFields = interestedJobCategories.map { it.jobCategory.jobGroup }.distinct(),
        employmentStatus = this.employmentStatus,
        company = this.company,
    )
