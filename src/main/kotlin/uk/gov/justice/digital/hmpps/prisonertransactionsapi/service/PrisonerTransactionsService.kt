package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.config.JwtService
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.email.EmailSender
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.Secret
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.SecretRepository
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.MagicLinkRequest
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.VerifyLinkRequest
import java.util.Optional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
class PrisonerTransactionsService(
  private val emailSender: EmailSender,
  private val jwtService: JwtService,
  private val secretRepository: SecretRepository,
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @Transactional
  fun generateMagicLink(request: MagicLinkRequest) {
    Secret(request.email, request.sessionID, generateSecret())
      .also { secret -> secretRepository.save(secret) }
      .also { secret -> emailSender.sendEmail(request.email, secret.secretValue) }
  }

  fun verifyMagicLink(request: VerifyLinkRequest): String =
    secretRepository.findById(request.email).toNullable()
      ?.also { secret -> secretRepository.delete(secret) }
      ?.takeIf { secret -> secret.sessionId == request.sessionID }
      ?.let { secret -> jwtService.generateToken(secret.email) }
      ?: throw EntityNotFoundException("Not found")

  private fun generateSecret() = UUID.randomUUID().toString()
}

fun <T> Optional<T>.toNullable(): T? = orElse(null)
