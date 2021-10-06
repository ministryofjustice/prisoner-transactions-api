package uk.gov.justice.digital.hmpps.prisonertransactionsapi.integration

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.email.EmailSender
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.CreateBarcodeResponse
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.VerifyLinkResponse
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.PrisonerTransactionsService
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.TokenService

class MagicLinkIntegrationTest : IntegrationTestBase() {

  @SpyBean
  private lateinit var spyEmailSender: EmailSender

  @Autowired
  private lateinit var prisonerTransactionsService: PrisonerTransactionsService

  @Autowired
  private lateinit var tokenService: TokenService

  @Test
  fun `can create barcode from magic link`() {
    doNothing().whenever(spyEmailSender).sendEmail(anyString(), anyString())

    webTestClient.post().uri("/link/email")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .body(BodyInserters.fromValue("""{ "email": "some.email@company.com" }"""))
      .exchange()
      .expectStatus().isOk

    val secretCaptor = argumentCaptor<String>()
    verify(spyEmailSender).sendEmail(eq("some.email@company.com"), secretCaptor.capture())
    val secret = secretCaptor.firstValue

    val verifyLinkResponse = webTestClient.post().uri("/link/verify")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .body(BodyInserters.fromValue("""{ "secret": "$secret" }"""))
      .exchange()
      .expectStatus().isOk
      .expectBody(VerifyLinkResponse::class.java)
      .returnResult().responseBody

    assertThat(tokenService.getTokenEmail(verifyLinkResponse.token)).isEqualTo("some.email@company.com")
    assertThat(prisonerTransactionsService.checkSecret(secret)).isFalse

    val createBarcodeResponse = webTestClient.post().uri("/barcode/prisoner/A1234AA")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .header("CREATE_BARCODE_TOKEN", verifyLinkResponse.token)
      .exchange()
      .expectStatus().isOk
      .expectBody(CreateBarcodeResponse::class.java)
      .returnResult().responseBody

    assertThat(createBarcodeResponse.barcode).isEqualTo("1234567890")
  }

  @Test
  fun `cannot create barcode without a valid token`() {
    webTestClient.post().uri("/barcode/prisoner/A1234AA")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .header("CREATE_BARCODE_TOKEN", "unknown token")
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  fun `cannot create barcode with a normal Auth token`() {
    webTestClient.post().uri("/barcode/prisoner/A1234AA")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isForbidden
  }
}
