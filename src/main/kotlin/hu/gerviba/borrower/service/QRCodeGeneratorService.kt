package hu.gerviba.borrower.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import hu.gerviba.borrower.config.AppConfig
import hu.gerviba.borrower.model.ResourceEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import javax.annotation.PostConstruct


@Service
class QRCodeGeneratorService(private val config: AppConfig) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        File(config.qrGenerationTarget).mkdirs()
    }

    @Throws(WriterException::class, IOException::class)
    private fun createQR(data: String, path: String) {
        val matrix = MultiFormatWriter().encode(
            String(data.toByteArray(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
            BarcodeFormat.QR_CODE,
            config.qrSize, config.qrSize,
            mutableMapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                EncodeHintType.CHARACTER_SET to StandardCharsets.UTF_8.toString(),
                EncodeHintType.MARGIN to 1
            )
        )

        MatrixToImageWriter.writeToPath(matrix,
            path.substring(path.lastIndexOf('.') + 1),
            Path.of(path))
    }

    fun generateForResource(resource: ResourceEntity) {
        if (resource.code.isEmpty())
            return

        val path = config.qrGenerationTarget + File.separator
        createQR("${config.appBaseUrl}/code/${resource.code}", "${path}${resource.id}-full.png")
        createQR(resource.code, "${path}${resource.id}.png")
        log.info("New QR code was generated to $path for user ${resource.id}")
    }

}
