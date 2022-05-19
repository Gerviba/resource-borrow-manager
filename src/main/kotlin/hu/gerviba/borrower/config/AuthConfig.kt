package hu.gerviba.borrower.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthenticationMethod
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import javax.annotation.PostConstruct

@Configuration
class AuthConfig(
    @Value("\${spring.security.oauth2.client.registration.authsch.client-id:}") private val id: String,
    @Value("\${spring.security.oauth2.client.registration.authsch.client-secret:}") private val secret: String,
    @Value("\${spring.security.oauth2.client.registration.authsch.redirect-uri:}") private val redirectUrl: String,
    @Value("\${spring.security.oauth2.client.provider.authsch.authorization-uri:}") private val authorizationUrl: String,
    @Value("\${spring.security.oauth2.client.provider.authsch.token-uri:}") private val tokenUri: String,
    private val config: AppConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        log.info("Config: $config")
    }

    @Bean
    fun clientRegistrationRepository(): ClientRegistrationRepository? {
        return InMemoryClientRegistrationRepository(authschClientRegistration())
    }

    private fun authschClientRegistration(): ClientRegistration? {
        return ClientRegistration.withRegistrationId("authsch")
            .clientId(id)
            .clientSecret(secret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri(redirectUrl)
            .scope("basic", "displayName", "sn", "givenName", "mail")
            .authorizationUri(authorizationUrl)
            .tokenUri(tokenUri)
            .userInfoUri(config.userInfoUri)
            .userInfoAuthenticationMethod(AuthenticationMethod.QUERY)
            .userNameAttributeName("internal_id")
            .clientName("AuthSch")
            .build()
    }

}
