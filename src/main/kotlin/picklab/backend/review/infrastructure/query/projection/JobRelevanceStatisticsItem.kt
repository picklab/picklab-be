package picklab.backend.review.infrastructure.query.projection

import com.querydsl.core.annotations.QueryProjection
import picklab.backend.review.application.query.model.JobRelevanceStatisticsView

@QueryProjection
data class JobRelevanceStatisticsItem(
    override val planningAvgScore: Double,
    override val developmentAvgScore: Double,
    override val marketingAvgScore: Double,
    override val aiAvgScore: Double,
    override val designAvgScore: Double,
) : JobRelevanceStatisticsView
