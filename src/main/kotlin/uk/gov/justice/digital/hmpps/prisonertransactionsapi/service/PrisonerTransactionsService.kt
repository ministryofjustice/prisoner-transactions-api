package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.PrisonerTransactionsRequest

@Service
class PrisonerTransactionsService() {

  val log: Logger = LoggerFactory.getLogger(this::class.java)
  @Transactional
  fun generateMagicLink(request: PrisonerTransactionsRequest) {
    log.info("generateMagicLink called....")
    log.info("Email id - "+ request.email)
  }
}
