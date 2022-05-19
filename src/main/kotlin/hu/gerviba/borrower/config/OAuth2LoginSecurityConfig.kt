package hu.gerviba.borrower.config

import com.fasterxml.jackson.databind.ObjectMapper
import hu.gerviba.borrower.dto.ProfileResponse
import hu.gerviba.borrower.exception.BusinessOperationFailedException
import hu.gerviba.borrower.model.UserEntity
import hu.gerviba.borrower.model.UserRole
import hu.gerviba.borrower.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.web.reactive.function.client.WebClient

const val PROFILE_RESPONSE_ATTRIBUTE_NAME = "profile"
const val USER_ENTITY_ATTRIBUTE_NAME = "entity"

val DefaultOAuth2User.profile : ProfileResponse
    get() = this.attributes[PROFILE_RESPONSE_ATTRIBUTE_NAME]!! as ProfileResponse

val DefaultOAuth2User.entity : UserEntity
    get() = this.attributes[USER_ENTITY_ATTRIBUTE_NAME]!! as UserEntity

fun Authentication.asUser() : DefaultOAuth2User = this.principal as DefaultOAuth2User

@Configuration
class OAuth2LoginSecurityConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val objectMapper: ObjectMapper,
    private val userService: UserService
) : WebSecurityConfigurerAdapter() {

    private val log = LoggerFactory.getLogger(javaClass)

    var userServiceClient = WebClient.builder()
        .baseUrl("https://auth.sch.bme.hu/api")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.USER_AGENT, "AuthSchKotlinAPI")
        .build()

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            /// GUEST
            .antMatchers(
                // Basic resources
                "/android-chrome-192x192.png", "/android-chrome-512x512.png", "/apple-touch-icon.png", "/browserconfig.xml",
                "/favicon.ico", "/favicon-16x16.png", "/favicon-32x32.png", "/mstile-150x150.png",
                "/noimage.png",
                "/noise.gif",
                "/safari-pinned-tab.svg", "/site.webmanifest",
                "/style.css",
                "/js/**",
                // MainController
                "/",
                "/search",
                "/res/**",
                "/identify/**",
                "/code/**",
                "/login/**",
                "/cdn/**"
            ).permitAll()
            /// REGULAR
            .antMatchers(
                // RegularController
                "/testing",
                "/loggedin",
                "/division/**",
                "/groups",
                "/group/**",
                "/resource/**",
                "/receive/**",
                "/resource-received/**",
                // UserController
                "/profile/**",
                "/user/**",
                "/control/logout",
                // GroupHandlerController
                "/requests",
                "/request/**",
                "/return/**",
                "/resource-returned/**",
                "/inventory/**"
            ).hasAnyRole(UserRole.REGULAR.name, UserRole.ADMIN.name, UserRole.SUPERUSER.name)
            // ADMIN and SUPERUSER
            .antMatchers("/admin/**").hasAnyRole(UserRole.ADMIN.name, UserRole.SUPERUSER.name)
            .antMatchers("/su/**").hasRole(UserRole.SUPERUSER.name)
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .authorizationEndpoint()
            .authorizationRequestResolver(
                CustomAuthorizationRequestResolver(
                    clientRegistrationRepository, "/oauth2/authorization"
                )
            )
            .and()
            .userInfoEndpoint()
            .userService { x ->
                // The API returns `test/json` which is an invalid mime type
                val profileJson: String? = userServiceClient.get()
                    .uri { uriBuilder ->
                        uriBuilder.path("/profile/")
                            .queryParam("access_token", x.accessToken.tokenValue)
                            .build()
                    }
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                val profile = objectMapper.readerFor(ProfileResponse::class.java).readValue<ProfileResponse>(profileJson)!!
                val userEntity = resolveEntity(profile)

                DefaultOAuth2User(
                    mutableListOf(SimpleGrantedAuthority("ROLE_${userEntity.role.name}")),
                    mapOf<String, Any>(
                        "internal_id" to (profile.internalId),
                        PROFILE_RESPONSE_ATTRIBUTE_NAME to profile,
                        USER_ENTITY_ATTRIBUTE_NAME to userEntity
                    ),
                    "internal_id"
                )
            }.and()
            .defaultSuccessUrl("/loggedin")
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .and()
            .csrf().ignoringAntMatchers("/api/**")
    }

    private fun resolveEntity(profile: ProfileResponse): UserEntity {
        val userIfExists = userService.getUserIfExists(profile.internalId)
        if (userIfExists.isPresent) {
            val user = userIfExists.orElseThrow()
            log.info("Loading user ${user.name} (${user.internalId}) locked:${user.locked}")
            if (user.locked)
                throw BusinessOperationFailedException("User is locked!")

            return user
        }

        log.info("Creating new user with name: ${profile.surname} ${profile.givenName} (${profile.internalId})")
        return userService.createUser(profile.internalId,
            "${profile.surname} ${profile.givenName}",
            profile.email ?: "")
    }

}

