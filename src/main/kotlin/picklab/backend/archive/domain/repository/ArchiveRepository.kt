package picklab.backend.archive.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import picklab.backend.archive.domain.entity.Archive

@Repository
interface ArchiveRepository : JpaRepository<Archive, Long> {

}