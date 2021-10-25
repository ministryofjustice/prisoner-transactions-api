package uk.gov.justice.digital.hmpps.prisonertransactionsapi.integration

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class BarcodeResourceTest : IntegrationTestBase() {

  @Test
  fun `ok for a valid barcode`() {
    val barcode = barcodeService.createBarcode("some_user", "a_prison")

    webTestClient.post().uri("""/barcode/$barcode""")
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
  }

  @Test
  fun `not found for an invalid barcode`() {
    webTestClient.post().uri("""/barcode/unknown""")
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isNotFound
  }
}
