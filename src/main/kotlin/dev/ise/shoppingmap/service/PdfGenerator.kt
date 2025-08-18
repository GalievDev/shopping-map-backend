package dev.ise.shoppingmap.service

import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import dev.ise.shoppingmap.dto.Capsule
import dev.ise.shoppingmap.repository.postgre.PostgresClothRepository
import dev.ise.shoppingmap.repository.postgre.PostgresImageRepository
import dev.ise.shoppingmap.repository.postgre.PostgresOutfitRepository
import io.ktor.util.*
import java.io.ByteArrayOutputStream

object PdfGenerator {
    suspend fun generateCapsulePDF(capsule: Capsule): ByteArray {
        val document = Document()
        val out = ByteArrayOutputStream()
        PdfWriter.getInstance(document, out)
        document.open()

        if (capsule.name.isNotBlank()) {
            val titlePara = Paragraph(capsule.name)
            titlePara.alignment = Element.ALIGN_CENTER
            document.add(titlePara)
            document.add(Paragraph("\n"))
        }

        val imageByte = PostgresImageRepository.getById(capsule.imageId)!!.bytes.decodeBase64Bytes()
        val image = Image.getInstance(imageByte)
        image.scaleToFit(150f, 150f)

        val mainTable = PdfPTable(2)
        mainTable.widthPercentage = 100f
        mainTable.setWidths(floatArrayOf(2f, 2f))

        val imageCell = PdfPCell(image, true)
        imageCell.border = PdfPCell.NO_BORDER
        mainTable.addCell(imageCell)

        val infoTable = PdfPTable(1)
        infoTable.widthPercentage = 100f

        infoTable.addCell(noBorderCell("Description: ${capsule.description}"))

        infoTable.addCell(noBorderCell("Contains outfits:"))
        capsule.outfits.forEach { outfitId ->
            val outfit = PostgresOutfitRepository.getById(outfitId)!!
            infoTable.addCell(noBorderCell("Outfit: ${outfit.name}"))

            outfit.clothes.forEach { clothId ->
                val cloth = PostgresClothRepository.getById(clothId)!!
                infoTable.addCell(noBorderCell(" - ${cloth.name} (${cloth.link})"))
            }
        }

        val infoCell = PdfPCell(infoTable)
        infoCell.border = PdfPCell.NO_BORDER
        mainTable.addCell(infoCell)

        document.add(mainTable)
        document.close()
        return out.toByteArray()
    }

    fun noBorderCell(text: String): PdfPCell {
        val cell = PdfPCell(Paragraph(text))
        cell.border = PdfPCell.NO_BORDER
        return cell
    }
}