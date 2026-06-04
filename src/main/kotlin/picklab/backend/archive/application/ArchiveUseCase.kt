package picklab.backend.archive.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.archive.domain.entity.ArchiveReferenceUrl
import picklab.backend.archive.domain.entity.ArchiveUploadFileUrl
import picklab.backend.archive.domain.enums.ArchiveSortType
import picklab.backend.archive.domain.service.ArchiveReferenceUrlService
import picklab.backend.archive.domain.service.ArchiveService
import picklab.backend.archive.domain.service.ArchiveUploadFileUrlService
import picklab.backend.archive.entrypoint.request.ArchiveCreateRequest
import picklab.backend.archive.entrypoint.request.ArchiveRecordUpdateRequest
import picklab.backend.archive.entrypoint.response.ArchiveActivityResponse
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.file.application.FileManagementService
import picklab.backend.member.domain.MemberService
import picklab.backend.participation.domain.service.ActivityParticipationService

@Component
class ArchiveUseCase(
    private val memberService: MemberService,
    private val archiveService: ArchiveService,
    private val participationService: ActivityParticipationService,
    private val archiveReferenceUrlService: ArchiveReferenceUrlService,
    private val archiveUploadFileUrlService: ArchiveUploadFileUrlService,
    private val fileManagementService: FileManagementService,
) {
    @Transactional
    fun createArchive(
        request: ArchiveCreateRequest,
        memberPrincipal: MemberPrincipal,
    ) {
        val member = memberService.findActiveMember(memberPrincipal.memberId)
        val participation = participationService.mustFindByIdAndMemberId(request.participationId, member.id)
        if (!participation.canArchive()) {
            throw BusinessException(ErrorCode.CANNOT_CREATE_ARCHIVE)
        }
        if (archiveService.existsActiveByParticipationId(participation.id)) {
            throw BusinessException(ErrorCode.ALREADY_EXISTS_ARCHIVE)
        }

        val permanentFileUrls =
            fileManagementService.verifyAndMoveTempFilesToPermanent(
                fileUrls = request.fileUrls,
                memberId = member.id,
                activityId = participation.activity.id,
                category = "archive",
            )

        val entity = request.toCreateEntity(participation)
        val archive = archiveService.save(entity)

        val referenceUrls = request.referenceUrls.map { url -> ArchiveReferenceUrl(archive, url) }
        val uploadedFileUrls = permanentFileUrls.map { url -> ArchiveUploadFileUrl(archive, url) }

        archiveReferenceUrlService.saveAll(referenceUrls)
        archiveUploadFileUrlService.saveAll(uploadedFileUrls)
    }

    @Transactional
    fun updateArchiveRecord(
        archiveId: Long,
        request: ArchiveRecordUpdateRequest,
        memberPrincipal: MemberPrincipal,
    ) {
        val member = memberService.findActiveMember(memberPrincipal.memberId)
        val archive = archiveService.mustFindByIdAndMember(archiveId, member)

        val finalFileUrls =
            fileManagementService.processUpdatedFileUrls(
                fileUrls = request.fileUrls,
                memberId = member.id,
                activityId = archive.participation.activity.id,
                category = "archive",
            )

        archive.updateRecord(
            activityRecord = request.activityRecord,
            role = request.role,
            detailRole = request.detailRole,
            userStartDate = request.startDate,
            userEndDate = request.endDate,
            customRole = request.customRole,
        )
        archiveService.save(archive)

        archiveReferenceUrlService.deleteByArchive(archive)
        archiveUploadFileUrlService.deleteByArchive(archive)

        archiveReferenceUrlService.saveAll(request.referenceUrls.map { url -> ArchiveReferenceUrl(archive, url) })
        archiveUploadFileUrlService.saveAll(finalFileUrls.map { url -> ArchiveUploadFileUrl(archive, url) })
    }

    @Transactional(readOnly = true)
    fun getArchiveList(
        activityType: ActivityType?,
        sort: ArchiveSortType,
        memberPrincipal: MemberPrincipal,
    ): List<ArchiveActivityResponse> {
        val member = memberService.findActiveMember(memberPrincipal.memberId)
        val participations =
            participationService.findCompletedForArchive(
                memberId = member.id,
                activityType = activityType,
                sort = sort.toSort(),
            )
        val archivesByParticipationId =
            archiveService
                .findAllByParticipationIds(participations.map { it.id })
                .associateBy { it.participation.id }

        return participations.map { participation ->
            ArchiveActivityResponse.from(
                participation = participation,
                archive = archivesByParticipationId[participation.id],
            )
        }
    }
}
