package picklab.backend.auth.domain

import java.time.LocalDate

interface OAuthUserInfo {
    fun getSocialId(): String

    fun getName(): String

    fun getEmail(): String

    fun getProfileImage(): String

    fun getBirthdate(): LocalDate?
}
