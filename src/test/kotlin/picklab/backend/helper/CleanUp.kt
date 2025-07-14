package picklab.backend.helper

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CleanUp(
    private val jdbcTemplate: JdbcTemplate,
) {
    @Transactional
    fun all() {
        val tables =
            listOf(
                "activity",
                "activity_group",
                "activity_job_category",
                "archive",
                "archive_reference_url",
                "archive_upload_file_url",
                "activity_bookmark",
                "job_category",
                "member",
                "member_agreement",
                "member_auth_code",
                "member_interest_job_category",
                "member_notification_preference",
                "member_verification",
                "member_withdrawal",
                "notification",
                "review",
                "social_login",
            )

        tables.forEach { table ->
            jdbcTemplate.execute("TRUNCATE TABLE $table")
        }
    }
}
