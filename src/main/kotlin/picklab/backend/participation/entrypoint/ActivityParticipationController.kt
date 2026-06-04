package picklab.backend.participation.entrypoint

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.participation.application.ActivityParticipationUseCase
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.entrypoint.request.UpdateApplicationStatusRequest
import picklab.backend.participation.entrypoint.request.UpdateProgressStatusRequest
import picklab.backend.participation.entrypoint.response.ActivityParticipationResultResponse
import picklab.backend.participation.entrypoint.response.ActivityParticipationSummaryResponse

@RestController
class ActivityParticipationController(
    private val activityParticipationUseCase: ActivityParticipationUseCase,
) : ActivityParticipationApi {
    @PostMapping("/v1/activities/{activityId}/participations")
    override fun create(
        @AuthenticationPrincipal member: MemberPrincipal,
        @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        activityParticipationUseCase.createAppliedParticipation(member.memberId, activityId)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseWrapper.success(SuccessCode.CREATE_ACTIVITY_PARTICIPATION))
    }

    @DeleteMapping("/v1/activities/{activityId}/participations")
    override fun cancel(
        @AuthenticationPrincipal member: MemberPrincipal,
        @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        activityParticipationUseCase.cancelAppliedParticipation(member.memberId, activityId)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.DELETE_ACTIVITY_PARTICIPATION))
    }

    @PatchMapping("/v1/activity-participations/{participationId}/application-status")
    override fun updateApplicationStatus(
        @AuthenticationPrincipal member: MemberPrincipal,
        @PathVariable participationId: Long,
        @Valid @RequestBody request: UpdateApplicationStatusRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        activityParticipationUseCase.updateApplicationStatus(
            memberId = member.memberId,
            participationId = participationId,
            applicationStatus = request.applicationStatus,
        )
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.UPDATE_ACTIVITY_PARTICIPATION))
    }

    @PatchMapping("/v1/activity-participations/{participationId}/progress-status")
    override fun updateProgressStatus(
        @AuthenticationPrincipal member: MemberPrincipal,
        @PathVariable participationId: Long,
        @Valid @RequestBody request: UpdateProgressStatusRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        activityParticipationUseCase.updateProgressStatus(
            memberId = member.memberId,
            participationId = participationId,
            progressStatus = request.progressStatus,
        )
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.UPDATE_ACTIVITY_PARTICIPATION))
    }

    @GetMapping("/v1/activity-participations/results")
    override fun getResults(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestParam(required = false) applicationStatus: List<ApplicationStatus>?,
        @RequestParam(defaultValue = "1") @Min(1) page: Int,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) size: Int,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityParticipationResultResponse>>> {
        val response =
            activityParticipationUseCase.getResults(
                memberId = member.memberId,
                applicationStatuses = applicationStatus,
                page = page,
                size = size,
            )
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ACTIVITY_PARTICIPATIONS, response))
    }

    @GetMapping("/v1/activity-participations/summary")
    override fun getSummary(
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<ActivityParticipationSummaryResponse>> {
        val response = activityParticipationUseCase.getSummary(member.memberId)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ACTIVITY_PARTICIPATION_SUMMARY, response))
    }
}
