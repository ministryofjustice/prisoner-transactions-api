package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TokenService {

  // TODO move the token store to a RedisTokenStore
  private val tokenStore = mutableMapOf<String, String>()

  fun generateSecret(): String {
    return UUID.randomUUID().toString()
  }

  // TODO generate a JWT that includes the email and an expiry
  fun generateToken(email: String) = generateSecret().also { tokenStore[it] = email }

  fun getTokenEmail(token: String) = tokenStore.get(token)
}
