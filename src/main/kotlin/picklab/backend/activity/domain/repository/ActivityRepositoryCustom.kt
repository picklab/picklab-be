package picklab.backend.activity.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.model.ActivitySearchCondition
import picklab.backend.activity.application.model.ActivityView

interface ActivityRepositoryCustom {
    fun getActivities(
        queryData: ActivitySearchCondition,
        pageable: PageRequest,
    ): Page<ActivityView>

    /**
     * 활동명 자동완성 검색
     * @param keyword 검색 키워드 (앞글자 매칭)
     * @param limit 최대 결과 수
     * @return 매칭되는 활동명 목록
     */
    fun findActivityTitlesForAutocomplete(
        keyword: String,
        limit: Int,
    ): List<String>
}
