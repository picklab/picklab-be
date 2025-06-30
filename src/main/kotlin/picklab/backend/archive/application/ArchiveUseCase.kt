package picklab.backend.archive.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.archive.domain.entity.ArchiveReferenceUrl
import picklab.backend.archive.domain.entity.ArchiveUploadFileUrl
import picklab.backend.archive.domain.service.ArchiveReferenceUrlService
import picklab.backend.archive.domain.service.ArchiveService
import picklab.backend.archive.domain.service.ArchiveUploadFileUrlService
import picklab.backend.archive.entrypoint.request.ArchiveCreateRequest
import picklab.backend.archive.entrypoint.request.ArchiveUpdateRequest
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.member.domain.MemberService

@Component
class ArchiveUseCase(
    private val memberService: MemberService,
    private val archiveService: ArchiveService,
    private val activityService: ActivityService,
    private val archiveReferenceUrlService: ArchiveReferenceUrlService,
    private val archiveUploadFileUrlService: ArchiveUploadFileUrlService,
) {
    @Transactional
    fun createArchive(
        request: ArchiveCreateRequest,
        memberPrincipal: MemberPrincipal,
    ) {
        val member = memberService.findActiveMember(memberPrincipal.memberId)
        val activity = activityService.mustFindById(request.activityId)

        val entity = request.toCreateEntity(member, activity)
        val archive = archiveService.save(entity)

        val referenceUrls = request.referenceUrls.map { url -> ArchiveReferenceUrl(archive, url) }
        val uploadedFileUrls = request.fileUrls.map { url -> ArchiveUploadFileUrl(archive, url) }

        archiveReferenceUrlService.saveAll(referenceUrls)
        archiveUploadFileUrlService.saveAll(uploadedFileUrls)
    }

    @Transactional
    fun updateArchive(
        archiveId: Long,
        request: ArchiveUpdateRequest,
        memberPrincipal: MemberPrincipal,
    ) {
        val member = memberService.findActiveMember(memberPrincipal.memberId)
        val archive = archiveService.mustFindByIdAndMember(archiveId, member)

        archive.update(
            activityProgressStatus = request.activityProgressStatus,
            passOrFailStatus = request.passOrFailStatus,
        )

        archiveService.save(archive)
    }
}
