package uk.gov.justice.digital.hmpps.prisonertransactionsapi.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

data class CreateBarcodeRequest(
  @Schema(description = "The user creating a barcode, probably an email address", example = "andrew.barret@company.com")
  @NotNull
  val userId: String,
  @Schema(description = "The prisoner ID the barcode is for")
  @NotNull
  val prisonerId: String
)
