package uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class BarcodeRepositoryTest {

  @Autowired
  private lateinit var barcodeRepository: BarcodeRepository

  @Autowired
  private lateinit var barcodeEventRepository: BarcodeEventRepository

  @Test
  fun `can create and retrieve a barcode`() {

    val barcode = barcodeRepository.save(Barcode(barcode = "SOME_BARCODE"))
    barcodeEventRepository.save(
      BarcodeEvent(
        barcode = barcode,
        userId = "some.user@domain.com",
        prison = "some-prison",
        prisonerId = "some-prisoner-id",
        status = BarcodeStatus.CREATED,
      )
    )

    val savedBarcode = barcodeRepository.findByBarcode("SOME_BARCODE") as Barcode
    val savedBarcodeEvents = barcodeEventRepository.findByBarcode(savedBarcode)
    assertThat(savedBarcodeEvents).extracting<String> { it.barcode.barcode }.containsExactly("SOME_BARCODE")
    assertThat(savedBarcodeEvents).extracting<String>(BarcodeEvent::userId).containsExactly("some.user@domain.com")
    assertThat(savedBarcodeEvents).extracting<String>(BarcodeEvent::prison).containsExactly("some-prison")
    assertThat(savedBarcodeEvents).extracting<String>(BarcodeEvent::prisonerId).containsExactly("some-prisoner-id")
    assertThat(savedBarcodeEvents).extracting<BarcodeStatus>(BarcodeEvent::status).containsExactly(BarcodeStatus.CREATED)
  }
}
