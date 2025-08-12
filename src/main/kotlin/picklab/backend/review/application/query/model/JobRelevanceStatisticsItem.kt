package picklab.backend.review.application.query.model

import com.querydsl.core.annotations.QueryProjection

data class JobRelevanceStatisticsItem
    @QueryProjection
    constructor(
        val planningAvgScore: Double,
        val developmentAvgScore: Double,
        val marketingAvgScore: Double,
        val aiAvgScore: Double,
        val designAvgScore: Double,
    )
