package uk.gov.justice.digital.hmpps.prisonertransactionsapi.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter
import javax.servlet.http.HttpServletRequest

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
class ResourceServerConfiguration(private val barcodeUserDetailsService: UserDetailsService) :
  WebSecurityConfigurerAdapter() {

  override fun configure(http: HttpSecurity) {
    http
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and().headers().frameOptions().sameOrigin()
      .and().csrf().disable()
      .addFilterAfter(createBarcodeAuthenticationFilter(), RequestHeaderAuthenticationFilter::class.java)
      .authorizeRequests { auth ->
        auth.antMatchers(
          "/webjars/**",
          "favicon.ico",
          "/health/**",
          "/info",
          "/swagger-resources/**",
          "/v3/api-docs/**",
          "/swagger-ui/**",
          "/swagger-ui.html",
          "/h2-console/**",
        ).permitAll().anyRequest().authenticated()
      }.oauth2ResourceServer().jwt().jwtAuthenticationConverter(AuthAwareTokenConverter())
  }

  @Bean
  fun createBarcodeAuthenticationFilter(): RequestHeaderAuthenticationFilter =
    CreateBarcodeAuthenticationFilter(createBarcodeAuthenticationManager())

  @Bean
  fun createBarcodeAuthenticationManager(): AuthenticationManager =
    ProviderManager(mutableListOf<AuthenticationProvider>(createBarcodePreAuthProvider()))

  @Bean
  fun createBarcodePreAuthProvider(): PreAuthenticatedAuthenticationProvider =
    PreAuthenticatedAuthenticationProvider().apply {
      setPreAuthenticatedUserDetailsService(
        createBarcodeUserDetailsServiceWrapper()
      )
    }

  @Bean
  fun createBarcodeUserDetailsServiceWrapper(): UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> =
    UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>().apply {
      setUserDetailsService(
        barcodeUserDetailsService
      )
    }
}

class CreateBarcodeAuthenticationFilter(createBarcodeAuthenticationManager: AuthenticationManager) : RequestHeaderAuthenticationFilter() {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  init {
    setPrincipalRequestHeader("CREATE_BARCODE_TOKEN")
    setAuthenticationManager(createBarcodeAuthenticationManager)
    setExceptionIfHeaderMissing(false)
  }

  override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any? {
    log.info("For request ${request.requestURI} found CREATE_BARCODE_TOKEN request header: ${request.getHeader("CREATE_BARCODE_TOKEN")}")
    return super.getPreAuthenticatedPrincipal(request)
  }
}
