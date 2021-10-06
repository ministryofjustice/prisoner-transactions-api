package uk.gov.justice.digital.hmpps.prisonertransactionsapi.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

data class VerifyLinkResponse(
  @Schema(description = "The token returned after verifying a magic link secret")
  @NotNull
  val token: String
)
