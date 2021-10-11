package uk.gov.justice.digital.hmpps.prisonertransactionsapi.config

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CreateBarcodeUserDetailsService(private val jwtService: JwtService) :
  UserDetailsService {
  override fun loadUserByUsername(token: String): UserDetails =
    token
      ?.takeIf { jwtService.validateToken(it) }
      ?.let { jwtService.subject(it) }
      ?.let { User(it, "n/a", mutableListOf(SimpleGrantedAuthority("ROLE_CREATE_BARCODE"))) }
      ?: throw UsernameNotFoundException("Token $token is invalid")
}
