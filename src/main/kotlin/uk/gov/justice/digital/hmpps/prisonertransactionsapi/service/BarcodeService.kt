package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.config.SecurityUserContext
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.Barcode
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeEvent
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeEventRepository
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeRepository
import uk.gov.justice.digital.hmpps.prisonertransactionsapi.jpa.BarcodeStatus
import java.awt.image.BufferedImage
import javax.persistence.EntityNotFoundException

@Service
class BarcodeService(
  private val barcodeRepository: BarcodeRepository,
  private val barcodeEventRepository: BarcodeEventRepository,
  private val barcodeGeneratorService: BarcodeGeneratorService,
  private val securityUserContext: SecurityUserContext,
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

  fun generateBarcodeImage(barcode: String): BufferedImage = barcodeGeneratorService.generateBarcodeImage(barcode)

  fun verifyBarcode(code: String) {
    barcodeRepository.findById(code).toNullable()
      ?.takeIf { barcode -> barcodeEventRepository.findByBarcodeAndStatus(barcode, BarcodeStatus.SCANNED).isEmpty() }
      ?.also { barcode -> createScannedEventFromCreatedEvent(barcode) }
      ?: also {
        createScannedEventForInvalidBarcode(code)
        throw EntityNotFoundException("The barcode is invalid")
      }
  }

  private fun createScannedEventFromCreatedEvent(barcode: Barcode) {
    barcodeEventRepository.findByBarcodeAndStatus(barcode, BarcodeStatus.CREATED).firstOrNull()
      ?.also { createdEvent ->
        barcodeEventRepository.save(
          BarcodeEvent(
            barcode = barcode,
            userId = securityUserContext.principal,
            status = BarcodeStatus.SCANNED,
            prisonerId = createdEvent.prisonerId,
            prison = createdEvent.prison,
          )
        )
      }
  }

  private fun createScannedEventForInvalidBarcode(code: String) {
    barcodeRepository.findById(code).orElseGet { barcodeRepository.save(Barcode(code = code)) }
      .also { barcode ->
        barcodeEventRepository.save(
          BarcodeEvent(
            barcode = barcode,
            userId = securityUserContext.principal,
            status = BarcodeStatus.SCANNED,
            prison = "n/a",
            prisonerId = "n/a"
          )
        )
      }
  }
}
