package uk.gov.justice.digital.hmpps.prisonertransactionsapi.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

data class CreateBarcodeResponse(
  @Schema(description = "The barcode created for the prisoner")
  @NotNull
  val barcode: String
)
