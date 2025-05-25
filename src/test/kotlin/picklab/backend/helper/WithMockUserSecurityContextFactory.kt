package picklab.backend.helper

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockUserSecurityContextFactory : WithSecurityContextFactory<WithMockUser> {
    override fun createSecurityContext(annotation: WithMockUser?): SecurityContext? {
        val context = SecurityContextHolder.createEmptyContext()

        val authentication = UsernamePasswordAuthenticationToken(null, null, listOf())
        context.authentication = authentication
        return context
    }
}
