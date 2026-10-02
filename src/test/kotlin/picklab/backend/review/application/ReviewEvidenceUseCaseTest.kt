package picklab.backend.review.application

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.file.application.FileStoragePort
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.job.domain.service.JobService
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.entity.Member
import picklab.backend.participation.domain.service.ActivityParticipationService
import picklab.backend.review.application.model.ReviewCreateCommand
import picklab.backend.review.application.model.ReviewUpdateCommand
import picklab.backend.review.application.service.ReviewEvidenceService
import picklab.backend.review.domain.entity.Review
import picklab.backend.review.domain.enums.ReviewApprovalStatus
import picklab.backend.review.domain.service.ReviewService
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ReviewEvidenceUseCaseTest {
    private val reviewService = mockk<ReviewService>()
    private val memberService = mockk<MemberService>()
    private val activityService = mockk<ActivityService>()
    private val participationService = mockk<ActivityParticipationService>()
    private val jobService = mockk<JobService>()
    private val storage = mockk<FileStoragePort>()
    private val useCase =
        ReviewUseCase(
            reviewService,
            memberService,
            activityService,
            participationService,
            mockk(),
            jobService,
            mockk(),
            ReviewEvidenceService(storage),
        )
    private val member = mockk<Member> { every { id } returns 1 }
    private val activity = mockk<Activity> { every { id } returns 10 }
    private val jobCategory = mockk<JobCategory>()
    private val tempKey = "temp/review/1/10/abcd1234_20261002_120000.jpg"
    private val permanentKey = tempKey.removePrefix("temp/")

    @BeforeEach
    fun setUp() {
        every { memberService.findActiveMember(1) } returns member
        every { activityService.mustFindById(10) } returns activity
        every { participationService.validateCanWriteReview(1, 10) } just runs
        every { reviewService.existsByActivityIdAndMemberId(10, 1) } returns false
        every { jobService.getJobCategoryByGroupAndDetail(JobGroup.DEVELOPMENT, null) } returns jobCategory
        every { reviewService.save(any()) } answers { firstArg() }
    }

    @Test
    fun `등록 시 확인된 영구 키만 PENDING 리뷰에 저장한다`() {
        stubUpload()
        val saved = slot<Review>()

        useCase.createReview(createCommand(tempKey))

        verify(exactly = 1) { reviewService.save(capture(saved)) }
        assertEquals(permanentKey, saved.captured.objectKey)
        assertEquals(ReviewApprovalStatus.PENDING, saved.captured.reviewApprovalStatus)
    }

    @Test
    fun `HEAD에서 파일이 없으면 리뷰를 저장하지 않는다`() {
        every { storage.verifyTempFileExists(tempKey) } throws BusinessException(ErrorCode.FILE_NOT_FOUND)

        val error = assertFailsWith<BusinessException> { useCase.createReview(createCommand(tempKey)) }

        assertEquals(ErrorCode.FILE_NOT_FOUND, error.errorCode)
        verify(exactly = 0) { reviewService.save(any()) }
        verify(exactly = 0) { storage.moveTempFileToPermanentKey(any()) }
    }

    @Test
    fun `파일 이동 실패 시에도 리뷰를 저장하지 않는다`() {
        every { storage.verifyTempFileExists(tempKey) } just runs
        every { storage.moveTempFileToPermanentKey(tempKey) } throws BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)

        assertFailsWith<BusinessException> { useCase.createReview(createCommand(tempKey)) }

        verify(exactly = 0) { reviewService.save(any()) }
    }

    @Test
    fun `인증 자료 없는 등록은 기존 정책대로 REJECTED다`() {
        val saved = slot<Review>()

        useCase.createReview(createCommand(null))

        verify(exactly = 1) { reviewService.save(capture(saved)) }
        assertNull(saved.captured.objectKey)
        assertEquals(ReviewApprovalStatus.REJECTED, saved.captured.reviewApprovalStatus)
        verify(exactly = 0) { storage.verifyTempFileExists(any()) }
    }

    @Test
    fun `기존 파일과 활동을 유지한 내용 수정은 APPROVED를 유지한다`() {
        val review = existingReview()
        every { reviewService.mustFindById(99) } returns review

        useCase.updateReview(updateCommand(permanentKey))

        assertEquals(permanentKey, review.objectKey)
        assertEquals("수정한 한줄평", review.summary)
        assertEquals(ReviewApprovalStatus.APPROVED, review.reviewApprovalStatus)
        verify(exactly = 0) { storage.verifyTempFileExists(any()) }
        verify(exactly = 0) { storage.moveTempFileToPermanentKey(any()) }
    }

    @Test
    fun `인증 자료 교체는 새 영구 키로 연결하고 PENDING으로 돌아간다`() {
        val review = existingReview("review/1/10/ffff1234_20261001_120000.jpg")
        every { reviewService.mustFindById(99) } returns review
        stubUpload()

        useCase.updateReview(updateCommand(tempKey))

        assertEquals(permanentKey, review.objectKey)
        assertEquals(ReviewApprovalStatus.PENDING, review.reviewApprovalStatus)
    }

    @Test
    fun `수정 인증 자료 검증 실패 시 기존 내용과 승인 상태를 변경하지 않는다`() {
        val review = existingReview()
        every { reviewService.mustFindById(99) } returns review
        every { storage.verifyTempFileExists(tempKey) } throws BusinessException(ErrorCode.FILE_NOT_FOUND)

        assertFailsWith<BusinessException> { useCase.updateReview(updateCommand(tempKey)) }

        assertEquals(permanentKey, review.objectKey)
        assertEquals("기존 한줄평", review.summary)
        assertEquals(ReviewApprovalStatus.APPROVED, review.reviewApprovalStatus)
    }

    @Test
    fun `인증 자료 연결을 제거하면 REJECTED로 변경한다`() {
        val review = existingReview()
        every { reviewService.mustFindById(99) } returns review

        useCase.updateReview(updateCommand(null))

        assertNull(review.objectKey)
        assertEquals(ReviewApprovalStatus.REJECTED, review.reviewApprovalStatus)
    }

    private fun stubUpload() {
        every { storage.verifyTempFileExists(tempKey) } just runs
        every { storage.moveTempFileToPermanentKey(tempKey) } returns permanentKey
    }

    private fun existingReview(key: String = permanentKey): Review =
        Review(
            overallScore = 3,
            infoScore = 3,
            difficultyScore = 3,
            benefitScore = 3,
            summary = "기존 한줄평",
            strength = "장점",
            weakness = "단점",
            jobRelevanceScore = 3,
            objectKey = key,
            reviewApprovalStatus = ReviewApprovalStatus.APPROVED,
            member = member,
            activity = activity,
            jobCategory = jobCategory,
        )

    private fun createCommand(key: String?): ReviewCreateCommand =
        ReviewCreateCommand(
            activityId = 10,
            memberId = 1,
            overallScore = 3,
            infoScore = 3,
            difficultyScore = 3,
            benefitScore = 3,
            summary = "한줄평",
            strength = "장점",
            weakness = "단점",
            tips = null,
            jobRelevanceScore = 3,
            objectKey = key,
            jobGroup = JobGroup.DEVELOPMENT,
            jobDetail = null,
        )

    private fun updateCommand(key: String?): ReviewUpdateCommand =
        ReviewUpdateCommand(
            id = 99,
            activityId = 10,
            memberId = 1,
            overallScore = 4,
            infoScore = 4,
            difficultyScore = 4,
            benefitScore = 4,
            summary = "수정한 한줄평",
            strength = "장점",
            weakness = "단점",
            tips = null,
            jobRelevanceScore = 4,
            objectKey = key,
            jobGroup = JobGroup.DEVELOPMENT,
            jobDetail = null,
        )
}
