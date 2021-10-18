package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.springframework.dao.DataIntegrityViolationException
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
          prison = "PLACEHOLDER_PRISON",
        )
      )
    }.barcode

  private fun createBarcode(): Barcode {
    var barcode: Barcode? = null
    while (barcode == null) {
      barcode = try {
        barcodeRepository.save(Barcode(barcode = barcodeGeneratorService.generateBarcode()))
      } catch (ex: DataIntegrityViolationException) { null } catch (ex: Exception) { throw ex }
    }
    return barcode
  }
}

@Service
class BarcodeGeneratorService {
  private val maxBarcode = 999_999_999_999

  fun generateBarcode(): String = Random.nextLong(maxBarcode).toString()
}
