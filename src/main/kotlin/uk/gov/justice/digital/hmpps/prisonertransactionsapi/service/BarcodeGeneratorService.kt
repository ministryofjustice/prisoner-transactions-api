package uk.gov.justice.digital.hmpps.prisonertransactionsapi.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.oned.Code128Writer
import net.sourceforge.barbecue.BarcodeFactory
import net.sourceforge.barbecue.BarcodeImageHandler
import org.krysalis.barcode4j.impl.code128.Code128Bean
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import kotlin.random.Random

interface BarcodeGeneratorService {
  fun generateBarcode(): String
  fun generateBarcodeImage(barcode: String): BufferedImage
}

abstract class BarcodeGeneratorServiceCode128 : BarcodeGeneratorService {
  private val maxBarcode = 999_999_999_999

  override fun generateBarcode(): String =
    Random.nextLong(maxBarcode).toString()
      .padStart(12, '0')
}

// This is the current favourite because it includes numbers with the barcode. The downside to this library is that it's 10 years old. But it has not dependencies so maybe that's okay.
@Service
class BarcodeGeneratorServiceBarcode4j : BarcodeGeneratorServiceCode128() {
  override fun generateBarcodeImage(barcode: String): BufferedImage =
    BitmapCanvasProvider(160, BufferedImage.TYPE_BYTE_BINARY, false, 0)
      .also { Code128Bean().generateBarcode(it, barcode) }
      .bufferedImage
}

// This barcode library ha a limitation that the fonts it requires to add numbers to the barcode are not supported on Linux. It's also very old.
class BarcodeGeneratorServiceBarbecue : BarcodeGeneratorServiceCode128() {
  override fun generateBarcodeImage(barcode: String): BufferedImage =
    BarcodeFactory.createCode128(barcode)
      .apply { isDrawingText = true }
      .let { BarcodeImageHandler.getImage(it) }
}

// This is a more modern library and is still has maintenance support but it doesn't have the option to attach the numbers to the barcode.
// If we went with this library we'd have to return the numbers and the image ByteArray in a response object which doesn't seem great to me.
class BarcodeGeneratorServiceZxing : BarcodeGeneratorServiceCode128() {
  override fun generateBarcodeImage(barcode: String): BufferedImage =
    Code128Writer().encode(barcode, BarcodeFormat.CODE_128, 300, 150)
      .let { MatrixToImageWriter.toBufferedImage(it) }
}
