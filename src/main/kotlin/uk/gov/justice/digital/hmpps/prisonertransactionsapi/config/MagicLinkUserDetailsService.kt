package uk.gov.justice.digital.hmpps.prisonertransactionsapi.config

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.TokenService

@Component
class MagicLinkUserDetailsService(private val tokenService: TokenService) : UserDetailsService {
  override fun loadUserByUsername(username: String): UserDetails {
    return tokenService.getTokenEmail(username)
      ?.let { User(it, "n/a", mutableListOf(SimpleGrantedAuthority("ROLE_BARCODE"))) }
      ?: throw UsernameNotFoundException("User $username not found")
  }
}
