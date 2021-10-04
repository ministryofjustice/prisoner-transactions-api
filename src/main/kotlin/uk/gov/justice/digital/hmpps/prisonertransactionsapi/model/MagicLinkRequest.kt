package uk.gov.justice.digital.hmpps.prisonertransactionsapi.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

data class MagicLinkRequest(
  @Schema(description = "The email address to send the magic link to", example = "andrew.barret@company.com")
  @NotNull
  val email: String
)
