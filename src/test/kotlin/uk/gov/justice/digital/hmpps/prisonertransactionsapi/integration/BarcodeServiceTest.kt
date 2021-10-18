package uk.gov.justice.digital.hmpps.prisonertransactionsapi.integration

import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.Barcode
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeEvent
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeEventRepository
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeRepository
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeStatus
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.BarcodeGeneratorService
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.BarcodeService

class BarcodeServiceTest : IntegrationTestBase() {

  @Autowired
  private lateinit var barcodeService: BarcodeService

  @SpyBean
  private lateinit var barcodeGeneratorService: BarcodeGeneratorService

  @Autowired
  private lateinit var barcodeRepository: BarcodeRepository

  @Autowired
  private lateinit var barcodeEventRepository: BarcodeEventRepository

  @Test
  fun `can create and retrieve a barcode with the service`() {

    val barcode = barcodeService.createBarcode(userId = "some.user@domain.com", prisonerId = "some-prisoner-id")

    val savedBarcode = barcodeRepository.findByBarcode(barcode) as Barcode
    val savedBarcodeEvents = barcodeEventRepository.findByBarcode(savedBarcode)
    assertThat(savedBarcodeEvents).extracting<String> { it.barcode.barcode }.containsExactly(barcode)
    assertThat(savedBarcodeEvents).extracting<String>(BarcodeEvent::userId).containsExactly("some.user@domain.com")
    assertThat(savedBarcodeEvents).extracting<String>(BarcodeEvent::prisonerId).containsExactly("some-prisoner-id")
    assertThat(savedBarcodeEvents).extracting<BarcodeStatus>(BarcodeEvent::status).containsExactly(BarcodeStatus.CREATED)
  }

  @Test
  fun `will retry if the barcode already exists`() {
    barcodeRepository.save(Barcode(barcode = "SOME_BARCODE"))
    whenever(barcodeGeneratorService.generateBarcode()).thenReturn("SOME_BARCODE").thenReturn("ANOTHER_BARCODE")

    val barcode = barcodeService.createBarcode(userId = "some.user@domain.com", prisonerId = "some-prisoner-id")

    val savedBarcode = barcodeRepository.findByBarcode(barcode) as Barcode
    val savedBarcodeEvents = barcodeEventRepository.findByBarcode(savedBarcode)

    assertThat(barcode).isEqualTo("ANOTHER_BARCODE")
    assertThat(savedBarcode.barcode).isEqualTo("ANOTHER_BARCODE")
    assertThat(savedBarcodeEvents).extracting<String> { it.barcode.barcode }.containsExactly("ANOTHER_BARCODE")
  }
}
