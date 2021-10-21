package uk.gov.justice.digital.hmpps.prisonertransactionsapi.integration

import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.Barcode
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeEvent
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeStatus
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.service.BarcodeService

class BarcodeServiceTest : IntegrationTestBase() {

  @Autowired
  private lateinit var barcodeService: BarcodeService

  @Test
  fun `can create and retrieve a barcode with the service`() {

    val barcode = barcodeService.createBarcode(userId = "some.user@domain.com", prisonerId = "a-prisoner")

    val savedBarcode = barcodeRepository.findById(barcode).orElseThrow()
    val savedBarcodeEvents = barcodeEventRepository.findByBarcode(savedBarcode)
    assertThat(savedBarcodeEvents).extracting<String> { it.barcode.code }.containsExactly(barcode)
    assertThat(savedBarcodeEvents).extracting<String>(BarcodeEvent::userId).containsExactly("some.user@domain.com")
    assertThat(savedBarcodeEvents).extracting<String>(BarcodeEvent::prisonerId).containsExactly("a-prisoner")
    assertThat(savedBarcodeEvents).extracting<BarcodeStatus>(BarcodeEvent::status).containsExactly(BarcodeStatus.CREATED)
  }

  @Test
  fun `will retry if the barcode already exists`() {
    barcodeRepository.save(Barcode(code = "SOME_CODE"))
    whenever(barcodeGeneratorService.generateBarcode()).thenReturn("SOME_CODE").thenReturn("ANOTHER_CODE")

    val barcode = barcodeService.createBarcode(userId = "some.user@domain.com", prisonerId = "a-prisoner")

    val savedBarcode = barcodeRepository.findById(barcode).orElseThrow()
    val savedBarcodeEvents = barcodeEventRepository.findByBarcode(savedBarcode)

    assertThat(barcode).isEqualTo("ANOTHER_CODE")
    assertThat(savedBarcode.code).isEqualTo("ANOTHER_CODE")
    assertThat(savedBarcodeEvents).extracting<String> { it.barcode.code }.containsExactly("ANOTHER_CODE")
  }
}
