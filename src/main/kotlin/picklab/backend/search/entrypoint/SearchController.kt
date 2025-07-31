package picklab.backend.search.entrypoint

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.search.application.SearchUseCase
import picklab.backend.search.entrypoint.response.AutocompleteResponse

@RestController
@RequestMapping("/v1/search")
class SearchController(
    private val searchUseCase: SearchUseCase,
) : SearchApi {
    
    @GetMapping("")
    override fun search(): String {
        // TODO: 검색 로직 구현 예정
        return "Search endpoint ready"
    }
    
    @GetMapping("/autocomplete")
    override fun autocomplete(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ResponseWrapper<AutocompleteResponse>> {
        val response = searchUseCase.getAutocompleteResults(keyword, limit)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.SEARCH_AUTOCOMPLETE_SUCCESS, response)
        )
    }
} 