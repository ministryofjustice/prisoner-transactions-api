package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.config.JwtService
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.email.EmailSender
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.MagicLinkRequest
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.VerifyLinkRequest
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
class PrisonerTransactionsService(
  private val emailSender: EmailSender,
  private val jwtService: JwtService,
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  // TODO move the secret store to a Redis cache
  private val secretStore = mutableMapOf<String, String>()

  @Transactional
  fun generateMagicLink(request: MagicLinkRequest) =
    generateSecret()
      .also { secretStore[it] = request.email }
      .also { emailSender.sendEmail(request.email, it) }

  fun verifyMagicLink(request: VerifyLinkRequest): String =
    secretStore[request.secret]
      ?.let { email -> jwtService.generateToken(email) }
      ?.also { secretStore.remove(request.secret) }
      ?: throw EntityNotFoundException("Not found")

  fun createBarcode(prisoner: String) = "1234567890"

  fun checkSecret(secret: String) = secretStore.containsKey(secret)

  private fun generateSecret() = UUID.randomUUID().toString()
}
