package picklab.backend.member.domain.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import picklab.backend.common.model.SoftDeleteEntity
import picklab.backend.member.domain.enums.EmploymentType
import picklab.backend.member.domain.enums.SocialType
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "member")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class Member(
    @Column(name = "name", length = 20, nullable = false)
    @Comment("회원 이름")
    var name: String,
    @Column(name = "email", length = 100, nullable = false)
    @Comment("회원 이메일")
    var email: String,
    @Column(name = "company", length = 50, nullable = false)
    @Comment("재직중인 회사")
    var company: String = "",
    @Column(name = "school", length = 50, nullable = false)
    @Comment("최종 학교")
    var school: String = "",
    @Column(name = "department", length = 50, nullable = false)
    @Comment("전공")
    var department: String = "",
    @Column(name = "birth_date")
    @Comment("생년월일")
    var birthDate: LocalDate? = null,
    @Column(name = "nickname", nullable = false, length = 50)
    @Comment("닉네임")
    var nickname: String = "",
    @Column(name = "profile_image_url", length = 255)
    @Comment("프로필 이미지 URL")
    var profileImageUrl: String = "",
    @Column(name = "education_level", nullable = false, length = 50)
    @Comment("최종 학력")
    var educationLevel: String = "",
    @Column(name = "graduation_status", nullable = false, length = 50)
    @Comment("학업 상태")
    var graduationStatus: String = "",
    @Column(name = "employment_status", nullable = false, length = 50)
    @Comment("재직 상태")
    var employmentStatus: String = "",
    @Column(name = "employment_type", length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("고용 형태")
    var employmentType: EmploymentType = EmploymentType.NONE,
    @Column(name = "is_completed", nullable = false)
    @Comment("회원 가입 완료 여부")
    var isCompleted: Boolean = false,
    @Column(name = "refresh_token")
    @Comment("리프레시 토큰")
    var refreshToken: String = "",
    @OneToMany(mappedBy = "member", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    val socialLogins: MutableList<SocialLogin> = mutableListOf(),
    @OneToMany(mappedBy = "member", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    val interestedJobCategories: MutableList<InterestedJobCategory> = mutableListOf(),
) : SoftDeleteEntity() {
    fun addSocialLogin(
        socialType: SocialType,
        socialId: String,
    ) {
        this.socialLogins.add(SocialLogin(this, socialType, socialId))
    }

    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    fun insertAdditionalInfo(
        nickname: String,
        educationLevel: String,
        school: String,
        graduationStatus: String,
        employmentStatus: String,
        company: String,
        employmentType: EmploymentType,
    ) {
        this.nickname = nickname
        this.educationLevel = educationLevel
        this.school = school
        this.graduationStatus = graduationStatus
        this.employmentStatus = employmentStatus
        this.company = company
        this.employmentType = employmentType
    }

    fun updateInfo(
        name: String,
        nickname: String,
        educationLevel: String,
        school: String,
        graduationStatus: String,
        employmentStatus: String,
        company: String,
        employmentType: EmploymentType,
    ) {
        this.name = name
        this.nickname = nickname
        this.educationLevel = educationLevel
        this.school = school
        this.graduationStatus = graduationStatus
        this.employmentStatus = employmentStatus
        this.company = company
        this.employmentType = employmentType
    }

    fun updateProfileImage(profileImage: String) {
        this.profileImageUrl = profileImage
    }

    fun updateEmail(email: String) {
        this.email = email
    }

    fun withdraw() {
        this.deletedAt = LocalDateTime.now()
    }
}
