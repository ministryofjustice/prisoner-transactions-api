package uk.gov.justice.digital.hmpps.prisonertransactionsapi.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

data class VerifyLinkRequest(
  @Schema(description = "The one time secret used to verify a magic link")
  @NotNull
  val secret: String,
  @Schema(description = "The browser session ID the link was requested for")
  @NotNull
  val sessionID: String
)
