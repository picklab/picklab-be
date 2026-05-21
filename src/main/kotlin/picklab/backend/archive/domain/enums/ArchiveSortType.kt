package picklab.backend.archive.domain.enums

import org.springframework.data.domain.Sort

enum class ArchiveSortType {
    LATEST,
    OLDEST,
    ;

    fun toSort(): Sort =
        when (this) {
            LATEST -> Sort.by(Sort.Direction.DESC, "createdAt")
            OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt")
        }
}
