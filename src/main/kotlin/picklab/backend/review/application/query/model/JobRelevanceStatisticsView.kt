package picklab.backend.review.application.query.model

interface JobRelevanceStatisticsView {
    val planningAvgScore: Double
    val developmentAvgScore: Double
    val marketingAvgScore: Double
    val aiAvgScore: Double
    val designAvgScore: Double
}
