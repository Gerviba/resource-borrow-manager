package hu.gerviba.borrower.config


import hu.gerviba.borrower.dto.Scope
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import javax.servlet.http.HttpServletRequest


class CustomAuthorizationRequestResolver(
    repo: ClientRegistrationRepository,
    authorizationRequestBaseUri: String
) : OAuth2AuthorizationRequestResolver {

    private var defaultResolver: OAuth2AuthorizationRequestResolver? = null

    init {
        defaultResolver = DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri)
    }

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        var req: OAuth2AuthorizationRequest? = defaultResolver?.resolve(request)
        if (req != null)
            req = customizeAuthorizationRequest(req)
        return req
    }

    override fun resolve(request: HttpServletRequest, clientRegistrationId: String): OAuth2AuthorizationRequest? {
        var req: OAuth2AuthorizationRequest? = defaultResolver?.resolve(request, clientRegistrationId)
        if (req != null)
            req = customizeAuthorizationRequest(req)
        return req
    }

    private fun customizeAuthorizationRequest(request: OAuth2AuthorizationRequest): OAuth2AuthorizationRequest? {
        return OAuth2AuthorizationRequest
            .from(request)
            .scopes(setOf(Scope.BASIC, Scope.DISPLAY_NAME, Scope.GIVEN_NAME, Scope.MAIL, Scope.SURNAME).map { it.scope }.toSet())
            .build()
    }

}
