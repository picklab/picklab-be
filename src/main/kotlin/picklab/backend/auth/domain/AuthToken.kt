package picklab.backend.auth.domain

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
)
