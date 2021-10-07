package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TokenService {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  // TODO move the token store to a RedisTokenStore
  private val tokenStore = mutableMapOf<String, String>()

  fun generateSecret(): String {
    return UUID.randomUUID().toString()
  }

  // TODO generate a JWT that includes the email and an expiry
  fun generateToken(email: String) =
    generateSecret()
      .also { tokenStore[it] = email }
      .also { log.info("Generated token $it for email $email") }

  fun getTokenEmail(token: String) =
    tokenStore.get(token)
      ?.also { log.info("Found email $it for token $token") }
}