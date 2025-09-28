package picklab.backend

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import picklab.backend.auth.domain.AuthException
import picklab.backend.auth.infrastructure.GithubUserInfo
import picklab.backend.auth.infrastructure.GoogleUserInfo
import picklab.backend.auth.infrastructure.KakaoUserInfo
import picklab.backend.auth.infrastructure.NaverUserInfo
import picklab.backend.common.model.ErrorCode

@DisplayName("소셜 로그인 유저 정보 매핑 테스트")
class SocialTypeUserInfoTest {
    @Nested
    @DisplayName("카카오 소셜 로그인 유저 정보 매핑 테스트")
    inner class KakaoUserInfoTest {
        @Nested
        @DisplayName("성공")
        inner class Success {
            @Test
            @DisplayName("카카오 소셜 로그인 매핑 테스트 - 성공")
            fun kakaoUserInfoMappingTest() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "properties": {
                        "nickname": "테스트",
                        "profile_image": "http://example.com/image.jpg"
                      },
                      "kakao_account": {
                        "email": "test@kakao.com"
                      }
                    }
                    """.trimIndent()

                val node = ObjectMapper().readTree(json)
                val kakaoUserInfo = KakaoUserInfo(node)

                // when
                val socialId = kakaoUserInfo.getSocialId()
                val name = kakaoUserInfo.getName()
                val email = kakaoUserInfo.getEmail()
                val profileImage = kakaoUserInfo.getProfileImage()

                // then
                assertThat(socialId).isEqualTo("10101")
                assertThat(name).isEqualTo("테스트")
                assertThat(email).isEqualTo("test@kakao.com")
                assertThat(profileImage).isEqualTo("http://example.com/image.jpg")
            }
        }

        @Nested
        @DisplayName("실패")
        inner class Failure {
            @Test
            @DisplayName("소셜ID가 없을 경우 - 예외발생")
            fun kakaoUserInfoFailWithSocialIdDoesntExist() {
                // given
                val json =
                    """
                    {
                      "properties": {
                        "nickname": "테스트",
                        "profile_image": "http://example.com/image.jpg"
                      },
                      "kakao_account": {
                        "email": "test@kakao.com"
                      }
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { KakaoUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_ID)
            }

            @Test
            @DisplayName("이름이 없을 경우 - 예외발생")
            fun kakaoUserInfoFailWithNameDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "properties": {
                        "profile_image": "http://example.com/image.jpg"
                      },
                      "kakao_account": {
                        "email": "test@kakao.com"
                      }
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { KakaoUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_NAME)
            }

            @Test
            @DisplayName("이메일이 없을 경우 - 예외발생")
            fun kakaoUserInfoFailWithEmailDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "properties": {
                        "nickname": "테스트",
                        "profile_image": "http://example.com/image.jpg"
                      }
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { KakaoUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_EMAIL)
            }

            @Test
            @DisplayName("프로필 이미지가 없을 경우 - 예외발생")
            fun kakaoUserInfoFailWithProfileImageDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "properties": {
                        "nickname": "테스트"
                      },
                      "kakao_account": {
                        "email": "test@kakao.com"
                      }
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { KakaoUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_PROFILE_IMAGE)
            }
        }
    }

    @Nested
    @DisplayName("구글 소셜 로그인 유저 정보 매핑 테스트")
    inner class GoogleUserInfoTest {
        @Nested
        @DisplayName("성공")
        inner class Success {
            @Test
            @DisplayName("구글 소셜 로그인 매핑 테스트")
            fun googleUserInfoTest() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "email": "test@gmail.com",
                      "picture": "https://example.com/profile.png",
                      "birthdate": "1990-01-01"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)
                val googleUserInfo = GoogleUserInfo(node)

                // then
                assertThat(googleUserInfo.getSocialId()).isEqualTo("10101")
                assertThat(googleUserInfo.getName()).isEqualTo("테스트")
                assertThat(googleUserInfo.getEmail()).isEqualTo("test@gmail.com")
                assertThat(googleUserInfo.getProfileImage()).isEqualTo("https://example.com/profile.png")
                assertThat(googleUserInfo.getBirthdate()).isEqualTo("1990-01-01")
            }
        }

        @Nested
        @DisplayName("실패")
        inner class Failure {
            @Test
            @DisplayName("소셜ID가 없을 경우 - 예외발생")
            fun googleUserInfoFailWithSocialIdDoesntExist() {
                val json =
                    """
                    {
                      "name": "테스트",
                      "email": "test@gmail.com",
                      "picture": "https://example.com/profile.png",
                      "birthdate": "1990-01-01"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { GoogleUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_ID)
            }

            @Test
            @DisplayName("이름이 없을 경우 - 예외발생")
            fun googleUserInfoFailWithNameDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "email": "test@gmail.com",
                      "picture": "https://example.com/profile.png",
                      "birthdate": "1990-01-01"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { GoogleUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_NAME)
            }

            @Test
            @DisplayName("이메일이 없을 경우 - 예외발생")
            fun googleUserInfoFailWithEmailDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "picture": "https://example.com/profile.png",
                      "birthdate": "1990-01-01"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { GoogleUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_EMAIL)
            }

            @Test
            @DisplayName("프로필 이미지가 없을 경우 - 예외발생")
            fun googleUserInfoFailWithProfileImageDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "email": "test@gmail.com",
                      "birthdate": "1990-01-01"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { GoogleUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_PROFILE_IMAGE)
            }

            @Test
            @DisplayName("생일이 없을 경우 - null return")
            fun googleUserInfoFailWithBirthDateDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "email": "test@gmail.com",
                      "picture": "https://example.com/profile.png"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)
                val googleUserInfo = GoogleUserInfo(node)

                // then
                val birthdate = googleUserInfo.getBirthdate()
                assertThat(birthdate).isNull()
            }
        }
    }

    @Nested
    @DisplayName("네이버 소셜 로그인 유저 정보 매핑 테스트")
    inner class NaverUserInfoTest {
        @Nested
        @DisplayName("성공")
        inner class Success {
            @Test
            @DisplayName("네이버 소셜 로그인 매핑 테스트")
            fun naverUserInfoTest() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "email": "test@naver.com",
                      "profile_image": "https://example.com/profile.jpg",
                      "birthyear": "1990",
                      "birthday": "01-23"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)
                val naverUserInfo = NaverUserInfo(node)

                // then
                assertThat(naverUserInfo.getSocialId()).isEqualTo("10101")
                assertThat(naverUserInfo.getName()).isEqualTo("테스트")
                assertThat(naverUserInfo.getEmail()).isEqualTo("test@naver.com")
                assertThat(naverUserInfo.getProfileImage()).isEqualTo("https://example.com/profile.jpg")
                assertThat(naverUserInfo.getBirthdate()).isEqualTo("1990-01-23")
            }

            @Test
            @DisplayName("생년이 없을 경우 - null return")
            fun googleUserInfoReturnNullWhenBirthYearDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "email": "test@naver.com",
                      "profile_image": "https://example.com/profile.jpg",
                      "birthday": "01-23"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)
                val naverUserInfo = NaverUserInfo(node)

                // then
                val birthdate = naverUserInfo.getBirthdate()
                assertThat(birthdate).isNull()
            }

            @Test
            @DisplayName("생일이 없을 경우 - null return")
            fun googleUserInfoReturnNullWhenBirthDayDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "email": "test@naver.com",
                      "profile_image": "https://example.com/profile.jpg",
                      "birthday": "01-23"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)
                val naverUserInfo = NaverUserInfo(node)

                // then
                val birthdate = naverUserInfo.getBirthdate()
                assertThat(birthdate).isNull()
            }
        }

        @Nested
        @DisplayName("실패")
        inner class Failure {
            @Test
            @DisplayName("소셜ID가 없을 경우 - 예외발생")
            fun googleUserInfoFailWithSocialIdDoesntExist() {
                // given
                val json =
                    """
                    {
                      "name": "테스트",
                      "email": "test@naver.com",
                      "profile_image": "https://example.com/profile.jpg",
                      "birthyear": "1990",
                      "birthday": "01-23"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { NaverUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_ID)
            }

            @Test
            @DisplayName("이름이 없을 경우 - 예외발생")
            fun googleUserInfoFailWithNameDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "email": "test@naver.com",
                      "profile_image": "https://example.com/profile.jpg",
                      "birthyear": "1990",
                      "birthday": "01-23"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { NaverUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_NAME)
            }

            @Test
            @DisplayName("이메일이 없을 경우 - 예외발생")
            fun googleUserInfoFailWithEmailDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "profile_image": "https://example.com/profile.jpg",
                      "birthyear": "1990",
                      "birthday": "01-23"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { NaverUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_EMAIL)
            }

            @Test
            @DisplayName("프로필 이미지가 없을 경우 - 예외발생")
            fun googleUserInfoFailWithProfileImageDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "email": "test@naver.com",
                      "birthyear": "1990",
                      "birthday": "01-23"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { NaverUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_PROFILE_IMAGE)
            }
        }
    }

    @Nested
    @DisplayName("깃허브 소셜 로그인 유저 정보 매핑 테스트")
    inner class GithubUserInfoTest {
        @Nested
        @DisplayName("성공")
        inner class Success {
            @Test
            @DisplayName("깃허브 소셜 로그인 매핑 테스트")
            fun githubUserInfoTest() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "email": "test@github.com",
                      "avatar_url": "https://avatars.test.com/10101"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)
                val githubUserInfo = GithubUserInfo(node)

                // then
                assertThat(githubUserInfo.getSocialId()).isEqualTo("10101")
                assertThat(githubUserInfo.getName()).isEqualTo("테스트")
                assertThat(githubUserInfo.getEmail()).isEqualTo("test@github.com")
                assertThat(githubUserInfo.getProfileImage()).isEqualTo("https://avatars.test.com/10101")
                assertThat(githubUserInfo.getBirthdate()).isNull()
            }
        }

        @Nested
        @DisplayName("실패")
        inner class Failure {
            @Test
            @DisplayName("소셜ID가 없을 경우 - 예외발생")
            fun githubUserInfoFailWithSocialIdDoesntExist() {
                // given
                val json =
                    """
                    {
                      "name": "테스트",
                      "email": "test@github.com",
                      "avatar_url": "https://avatars.test.com/10101"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { GithubUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_ID)
            }

            @Test
            @DisplayName("이름이 없을 경우 - 예외발생")
            fun githubUserInfoFailWithNameDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "email": "test@github.com",
                      "avatar_url": "https://avatars.test.com/10101"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { GithubUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_NAME)
            }

            @Test
            @DisplayName("이메일이 없을 경우 - 예외발생")
            fun githubUserInfoFailWithEmailDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "avatar_url": "https://avatars.test.com/10101"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { GithubUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_EMAIL)
            }

            @Test
            @DisplayName("프로필 이미지가 없을 경우 - 예외발생")
            fun githubUserInfoFailWithProfileImageDoesntExist() {
                // given
                val json =
                    """
                    {
                      "id": "10101",
                      "name": "테스트",
                      "email": "test@github.com"
                    }
                    """.trimIndent()

                // when
                val node = ObjectMapper().readTree(json)

                // then
                val exception = assertThrows<AuthException> { GithubUserInfo(node) }
                assertThat(exception.errorCode).isEqualTo(ErrorCode.EMPTY_SOCIAL_PROFILE_IMAGE)
            }
        }
    }
}
