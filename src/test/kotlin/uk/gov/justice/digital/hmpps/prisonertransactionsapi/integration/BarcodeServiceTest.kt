package uk.gov.justice.digital.hmpps.prisonertransactionsapi.integration

import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.Barcode
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeEvent
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeStatus
import javax.persistence.EntityNotFoundException

class BarcodeServiceTest : IntegrationTestBase() {

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

  @Test
  fun `can create and scan a barcode with the service`() {
    val barcode = barcodeService.createBarcode(userId = "some.user@domain.com", prisonerId = "a-prisoner")

    try {
      barcodeService.verifyBarcode(barcode)
    } catch (ex: Exception) {
      fail("The barcode should have been verified as ok", ex)
    }
  }

  @Test
  fun `will fail if scanning an unknown barcode`() {
    assertThatThrownBy { barcodeService.verifyBarcode("UNKNOWN_CODE") }
      .isInstanceOf(EntityNotFoundException::class.java)
  }

  @Test
  fun `cannot generate new barcode for previous scan failure`() {
    assertThatThrownBy { barcodeService.verifyBarcode("UNKNOWN_CODE") }
      .isInstanceOf(EntityNotFoundException::class.java)

    whenever(barcodeGeneratorService.generateBarcode()).thenReturn("UNKNOWN_CODE").thenReturn("ANOTHER_CODE")

    val barcode = barcodeService.createBarcode(userId = "some.user@domain.com", prisonerId = "a-prisoner")
    assertThat(barcode).isEqualTo("ANOTHER_CODE")
  }
}
