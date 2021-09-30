package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.email.EmailSender
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.PrisonerTransactionsRequest
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.util.TokenGenerator

@Service
class PrisonerTransactionsService(
  private val emailSender: EmailSender,
  private val tokenGenerator: TokenGenerator
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @Transactional
  fun generateMagicLink(request: PrisonerTransactionsRequest) {
    log.info("generateMagicLink called....")
    log.info("Email id - " + request.email)
    val secret = tokenGenerator.generateSecret()
    log.info("Generated secret - $secret")
    emailSender.sendEmail(request.email, secret)
  }
}
