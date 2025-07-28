package picklab.backend.search.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "검색 기록 응답")
data class SearchHistoryResponse(
    @Schema(description = "검색 기록 ID", example = "1")
    val id: Long,
    
    @Schema(description = "검색 키워드", example = "스프링 부트 개발자")
    val keyword: String,
    
    @Schema(description = "검색 실행 시간", example = "2024-01-15T10:30:00")
    val searchedAt: LocalDateTime,
    
    @Schema(description = "생성 시간", example = "2024-01-15T10:30:00")
    val createdAt: LocalDateTime
) 