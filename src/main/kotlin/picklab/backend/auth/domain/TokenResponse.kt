package picklab.backend.auth.domain

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
