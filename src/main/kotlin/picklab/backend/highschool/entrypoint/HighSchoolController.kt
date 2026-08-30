package picklab.backend.highschool.entrypoint

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.highschool.application.HighSchoolUseCase
import picklab.backend.highschool.entrypoint.response.HighSchoolSearchResponse

@RestController
@RequestMapping("/v1/high-schools")
class HighSchoolController(
    private val highSchoolUseCase: HighSchoolUseCase,
) : HighSchoolApi {
    @GetMapping
    override fun search(
        @RequestParam(required = false) query: String?,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<ResponseWrapper<HighSchoolSearchResponse>> {
        val response =
            highSchoolUseCase
                .search(query = query, size = size)
                .let(HighSchoolSearchResponse::from)

        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.HIGH_SCHOOLS_RETRIEVED, response),
        )
    }
}
