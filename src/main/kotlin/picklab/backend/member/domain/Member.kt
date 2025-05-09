package picklab.backend.member.domain

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import picklab.backend.common.model.SoftDeleteEntity
import java.time.LocalDate

@Entity
@Table(name = "member")
class Member(
    @Column(name = "name", length = 20, nullable = false)
    @Comment("회원 이름")
    val name: String,
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
    @Column(name = "education_level", nullable = false, length = 50)
    @Comment("최종 학력")
    var educationLevel: String = "",
    @Column(name = "gradation_status", nullable = false, length = 50)
    @Comment("학업 상태")
    var gradationStatus: String = "",
    @Column(name = "employment_status", nullable = false, length = 50)
    @Comment("재직 상태")
    var employmentStatus: String = "",
    @Column(name = "is_completed", nullable = false)
    @Comment("회원 가입 완료 여부")
    var isCompleted: Boolean = false,
    @OneToMany(mappedBy = "member", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    val socialLogins: MutableList<SocialLogin> = mutableListOf(),
    @OneToMany(mappedBy = "member", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    val interestedJobCategories: MutableList<InterestedJobCategory> = mutableListOf(),
) : SoftDeleteEntity()
