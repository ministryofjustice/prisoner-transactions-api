package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.Barcode
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeEvent
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeEventRepository
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeRepository
import kotlin.random.Random

@Service
class BarcodeService(
  private val barcodeRepository: BarcodeRepository,
  private val barcodeEventRepository: BarcodeEventRepository,
  private val barcodeGeneratorService: BarcodeGeneratorService,
) {

  fun createBarcode(userId: String, prisonerId: String): String =
    createBarcode().also {
      barcodeEventRepository.save(
        BarcodeEvent(
          barcode = it,
          userId = userId,
          prisonerId = prisonerId,
          prison = "LEI",
        )
      )
    }.code

  private fun createBarcode(): Barcode {
    var barcode = barcodeGeneratorService.generateBarcode()
    while (barcodeRepository.existsById(barcode)) {
      barcode = barcodeGeneratorService.generateBarcode()
    }
    return barcodeRepository.save(Barcode(barcode))
  }
}

@Service
class BarcodeGeneratorService {
  private val maxBarcode = 999_999_999_999

  fun generateBarcode(): String = Random.nextLong(maxBarcode).toString()
}
