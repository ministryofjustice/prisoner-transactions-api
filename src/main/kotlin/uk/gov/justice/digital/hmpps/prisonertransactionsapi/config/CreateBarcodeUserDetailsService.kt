package uk.gov.justice.digital.hmpps.prisonertransactionsapi.config

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.TokenService

@Component
class CreateBarcodeUserDetailsService(private val tokenService: TokenService) : UserDetailsService {
  // TODO check the expiry of the token and throw if it has expired
  override fun loadUserByUsername(token: String): UserDetails =
    tokenService.getTokenEmail(token)
      ?.let { User(it, "n/a", mutableListOf(SimpleGrantedAuthority("ROLE_CREATE_BARCODE"))) }
      ?: throw UsernameNotFoundException("User $token not found")
}
