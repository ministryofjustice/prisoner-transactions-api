package uk.gov.justice.digital.hmpps.prisonertransactionsapi.util

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TokenGenerator {
  fun generateSecret(): String {
    return UUID.randomUUID().toString()
  }
}
