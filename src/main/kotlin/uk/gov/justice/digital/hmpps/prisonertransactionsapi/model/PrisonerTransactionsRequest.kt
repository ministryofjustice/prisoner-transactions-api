package uk.gov.justice.digital.hmpps.prisonertransactionsapi.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

data class PrisonerTransactionsRequest(
  @Schema(description = "The version of licence conditions currently active as a string value", example = "1.0")
  @NotNull
  val email: String
)
