package picklab.backend.member.domain

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import picklab.backend.common.model.SoftDeleteEntity
import java.time.LocalDate

@Entity
@Table(name = "member")
class Member(
    @Column(name = "name", nullable = false)
    @Comment("회원 이름")
    val name: String,

    @Column(name = "email", nullable = false)
    @Comment("회원 이메일")
    val email: String,

    @Column(name = "company")
    @Comment("재직중인 회사")
    val company: String = "",

    @Column(name = "school")
    @Comment("최종 학교")
    val school: String = "",

    @Column(name = "department")
    @Comment("전공")
    val department: String = "",

    @Column
    @Comment("생년월일")
    var birthDate: LocalDate? = null,

    @Column(nullable = false, length = 50)
    @Comment("닉네임")
    var nickname: String = "",

    @Column(nullable = false, length = 50)
    @Comment("최종 학력")
    var educationLevel: String = "",

    @Column(nullable = false, length = 50)
    @Comment("학업 상태")
    var gradationStatus: String = "",

    @Column(nullable = false, length = 50)
    @Comment("재직 상태")
    var employmentStatus: String = "",

    @Column(nullable = false)
    @Comment("회원 가입 완료 여부")
    var isCompleted: Boolean = false,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    val socialLogins: MutableList<SocialLogin> = mutableListOf(),
) : SoftDeleteEntity()