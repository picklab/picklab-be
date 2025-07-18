package picklab.backend.activity.application

interface ViewCountLimiterPort {
    companion object {
        const val MAX_VIEW_ATTEMPTS = 10
        const val CACHE_NAME = "activityViewCount"
    }

    /**
     * 특정 활동에 대한 조회수 증가가 허용되는지 확인합니다.
     */
    fun isViewCountUpAllowed(
        activityId: Long,
        viewerIdentifier: String,
    ): Boolean
}
