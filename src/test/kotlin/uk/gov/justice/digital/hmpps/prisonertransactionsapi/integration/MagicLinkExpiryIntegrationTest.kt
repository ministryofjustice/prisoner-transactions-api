package uk.gov.justice.digital.hmpps.prisonertransactionsapi.integration

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.email.EmailSender

@ExtendWith(RedisExtension::class)
@TestPropertySource(
  properties = [
    "secret.expiry=1s"
  ]
)
class MagicLinkExpiryIntegrationTest : IntegrationTestBase() {

  @SpyBean
  private lateinit var spyEmailSender: EmailSender

  @Test
  fun `magic link is invalid if it has expired`() {
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

    Thread.sleep(1100L)

    webTestClient.post().uri("/link/verify")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .body(BodyInserters.fromValue("""{ "secret": "$secret", "sessionID": "some-session", "email": "some.email@company.com" }"""))
      .exchange()
      .expectStatus().isNotFound
  }
}
