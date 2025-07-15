package picklab.backend.review.application

import org.springframework.stereotype.Component
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.member.domain.entity.Member
import picklab.backend.review.application.model.ReviewCreateCommand
import picklab.backend.review.domain.entity.Review

@Component
class ReviewCreateConverter {
    fun toEntity(
        command: ReviewCreateCommand,
        member: Member,
        activity: Activity,
    ): Review =
        Review.create(
            overallScore = command.overallScore,
            infoScore = command.infoScore,
            difficultyScore = command.difficultyScore,
            benefitScore = command.benefitScore,
            summary = command.summary,
            strength = command.strength,
            weakness = command.weakness,
            tips = command.tips,
            jobRelevanceScore = command.jobRelevanceScore,
            url = command.url,
            member = member,
            activity = activity,
        )
}
