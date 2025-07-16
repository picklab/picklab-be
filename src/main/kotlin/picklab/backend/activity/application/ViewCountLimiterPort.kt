package picklab.backend.activity.application

interface ViewCountLimiterPort {
    /**
     * 특정 활동에 대한 조회수 증가가 허용되는지 확인합니다.
     */
    fun isViewCountUpAllowed(
        activityId: Long,
        viewerIdentifier: String,
    ): Boolean
}
