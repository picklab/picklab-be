package picklab.backend.university.entrypoint

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.university.application.UniversityUseCase
import picklab.backend.university.entrypoint.response.UniversitySearchResponse

@RestController
@RequestMapping("/v1/universities")
class UniversityController(
    private val universityUseCase: UniversityUseCase,
) : UniversityApi {
    @GetMapping
    override fun search(
        @RequestParam(required = false) query: String?,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<ResponseWrapper<UniversitySearchResponse>> {
        val response =
            universityUseCase
                .search(query = query, size = size)
                .let(UniversitySearchResponse::from)

        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.UNIVERSITIES_RETRIEVED, response),
        )
    }
}
