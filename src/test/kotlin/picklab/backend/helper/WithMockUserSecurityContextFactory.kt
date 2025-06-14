package picklab.backend.helper

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory
import picklab.backend.common.model.MemberPrincipal

class WithMockUserSecurityContextFactory : WithSecurityContextFactory<WithMockUser> {
    override fun createSecurityContext(annotation: WithMockUser?): SecurityContext? {
        val context = SecurityContextHolder.createEmptyContext()

        val principal = MemberPrincipal(1L)
        val authentication = UsernamePasswordAuthenticationToken(principal, null, listOf())
        context.authentication = authentication
        return context
    }
}
