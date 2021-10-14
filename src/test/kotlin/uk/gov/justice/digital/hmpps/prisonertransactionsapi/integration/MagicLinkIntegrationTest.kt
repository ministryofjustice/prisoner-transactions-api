package uk.gov.justice.digital.hmpps.prisonertransactionsapi.integration

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.config.JwtService
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.email.EmailSender
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.CreateBarcodeResponse
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.model.VerifyLinkResponse

@ExtendWith(RedisExtension::class)
class MagicLinkIntegrationTest : IntegrationTestBase() {

  @SpyBean
  private lateinit var spyEmailSender: EmailSender

  @Autowired
  private lateinit var jwtService: JwtService

  @Test
  fun `can create barcode from magic link`() {
    doNothing().whenever(spyEmailSender).sendEmail(anyString(), anyString())

    webTestClient.post().uri("/link/email")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .body(BodyInserters.fromValue("""{ "email": "some.email@company.com", "sessionID": "some-session" }"""))
      .exchange()
      .expectStatus().isOk

    val secretCaptor = argumentCaptor<String>()
    verify(spyEmailSender).sendEmail(eq("some.email@company.com"), secretCaptor.capture())
    val secret = secretCaptor.firstValue

    val verifyLinkResponse = webTestClient.post().uri("/link/verify")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .body(BodyInserters.fromValue("""{ "secret": "$secret", "sessionID": "some-session", "email": "some.email@company.com" }"""))
      .exchange()
      .expectStatus().isOk
      .expectBody(VerifyLinkResponse::class.java)
      .returnResult().responseBody

    assertThat(jwtService.subject(verifyLinkResponse.token)).isEqualTo("some.email@company.com")

    val createBarcodeResponse = webTestClient.post().uri("/barcode/prisoner/A1234AA")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .header("Create-Barcode-Token", verifyLinkResponse.token)
      .exchange()
      .expectStatus().isOk
      .expectBody(CreateBarcodeResponse::class.java)
      .returnResult().responseBody

    assertThat(createBarcodeResponse.barcode).isEqualTo("1234567890")

    webTestClient.post().uri("/link/verify")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .body(BodyInserters.fromValue("""{ "secret": "$secret", "sessionID": "some-session", "email": "some.email@company.com" }"""))
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `magic link is deleted if wrong session ID is sent in request`() {
    doNothing().whenever(spyEmailSender).sendEmail(anyString(), anyString())

    webTestClient.post().uri("/link/email")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .body(BodyInserters.fromValue("""{ "email": "some.email@company.com", "sessionID": "some-session" }"""))
      .exchange()
      .expectStatus().isOk

    val secretCaptor = argumentCaptor<String>()
    verify(spyEmailSender).sendEmail(eq("some.email@company.com"), secretCaptor.capture())
    val secret = secretCaptor.firstValue

    webTestClient.post().uri("/link/verify")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .body(BodyInserters.fromValue("""{ "secret": "$secret", "sessionID": "wrong-session", "email": "some.email@company.com" }"""))
      .exchange()
      .expectStatus().isNotFound

    webTestClient.post().uri("/link/verify")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .body(BodyInserters.fromValue("""{ "secret": "$secret", "sessionID": "some-session", "email": "some.email@company.com" }"""))
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `cannot create barcode without a valid token`() {
    webTestClient.post().uri("/barcode/prisoner/A1234AA")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .header("Create-Barcode-Token", "unknown token")
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
