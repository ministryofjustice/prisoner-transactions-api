package uk.gov.justice.digital.hmpps.prisonertransactionsapi.integration

import io.lettuce.core.ClientOptions
import io.lettuce.core.ClientOptions.DisconnectedBehavior
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.email.EmailSender
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeEventRepository
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeRepository
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.BarcodeGeneratorService

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(RedisExtension::class)
@Import(IntegrationTestBase.RedisConfig::class)
abstract class IntegrationTestBase {
  @Suppress("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  protected lateinit var jwtAuthHelper: JwtAuthHelper

  @SpyBean
  protected lateinit var spyEmailSender: EmailSender

  @SpyBean
  protected lateinit var barcodeGeneratorService: BarcodeGeneratorService

  @Autowired
  protected lateinit var barcodeRepository: BarcodeRepository

  @Autowired
  protected lateinit var barcodeEventRepository: BarcodeEventRepository

  @AfterEach
  fun `clear database`() {
    barcodeEventRepository.deleteAll()
    barcodeRepository.deleteAll()
  }

  internal fun setAuthorisation(
    user: String = "prisoner-transactions-admin",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf()
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisation(user, roles, scopes)

  @TestConfiguration
  class RedisConfig {
    @Bean
    fun lettuceClientConfigurationBuilderCustomizer(): LettuceClientConfigurationBuilderCustomizer =
      LettuceClientConfigurationBuilderCustomizer {
        it.clientOptions(
          ClientOptions.builder()
            .disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS)
            .build()
        )
      }
  }

  companion object {
    private val pgContainer = PostgresContainer.instance

    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      pgContainer?.run {
        registry.add("spring.datasource.url", pgContainer::getJdbcUrl)
        registry.add("spring.datasource.username", pgContainer::getUsername)
        registry.add("spring.datasource.password", pgContainer::getPassword)
        registry.add("spring.datasource.placeholders.database_update_password", pgContainer::getPassword)
        registry.add("spring.datasource.placeholders.database_read_only_password", pgContainer::getPassword)
        registry.add("spring.flyway.url", pgContainer::getJdbcUrl)
        registry.add("spring.flyway.user", pgContainer::getUsername)
        registry.add("spring.flyway.password", pgContainer::getPassword)
      }
    }
  }
}
