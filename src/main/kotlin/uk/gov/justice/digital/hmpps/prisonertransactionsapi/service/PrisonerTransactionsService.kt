package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.email.EmailSender
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.MagicLinkRequest
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.VerifyLinkRequest
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.util.TokenGenerator
import javax.persistence.EntityNotFoundException

@Service
class PrisonerTransactionsService(
  private val emailSender: EmailSender,
  private val tokenGenerator: TokenGenerator
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  // TODO move the secret store to a Redis cache
  private val secretStore = mutableMapOf<String, String>()
  // TODO move the token store to a RedisTokenStore
  private val tokenStore = mutableMapOf<String, String>()

  @Transactional
  fun generateMagicLink(request: MagicLinkRequest) {
    log.info("generateMagicLink called....")
    log.info("Email id - " + request.email)
    val secret = tokenGenerator.generateSecret()
    log.info("Generated secret - $secret")
    secretStore[secret] = request.email
    emailSender.sendEmail(request.email, secret)
  }

  fun verifyMagicLink(request: VerifyLinkRequest): String =
    secretStore[request.secret]
      ?.let { email ->
        // TODO generate a JWT that includes the email and an expiry
        val token = tokenGenerator.generateSecret()
        tokenStore[token] = email
        token
      }
      ?: throw EntityNotFoundException("Not found")

  fun getTokenEmail(token: String) = tokenStore.get(token)
}
