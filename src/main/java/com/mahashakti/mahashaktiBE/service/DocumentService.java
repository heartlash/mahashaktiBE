package com.mahashakti.mahashaktiBE.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@Slf4j
public class DocumentService {

    public byte[] generateDocument(String name, String details, List<String> headers, List<List<String>> data) {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Create a PDF writer
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);

            // Initialize the PDF document
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdfDoc);


            String logoPath = "logo.png";
            ClassPathResource resource = new ClassPathResource(logoPath);
            // Replace with actual image path
            ImageData imageData = ImageDataFactory.create(resource.getInputStream().readAllBytes());
            Image logo = new Image(imageData).scaleToFit(60, 60).setFixedPosition(pdfDoc.getDefaultPageSize().getWidth() - 110, pdfDoc.getDefaultPageSize().getHeight() - 110);
            document.add(logo);

            // Add Title
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Paragraph title = new Paragraph("MAHASHAKTI")
                    .setFont(font)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10);
            document.add(title);

            Paragraph subtitle = new Paragraph(name)
                    .setFont(font)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(2);
            document.add(subtitle);

            // Add Details (Subtitle)
            Paragraph detail = new Paragraph(details)
                    .setFont(font)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2)
                    .setMarginBottom(20);

            document.add(detail);

            // Auto-adjusting table width to fill the page width
            Table table = new Table(UnitValue.createPercentArray(headers.size())).useAllAvailableWidth();

            // Add headers with styling
            for (String header : headers) {
                Cell headerCell = new Cell().add(new Paragraph(header).setFont(font).setFontSize(12))
                        .setBackgroundColor(ColorConstants.YELLOW)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5);
                table.addHeaderCell(headerCell);
            }

            // Add data to the table
            for (List<String> row : data) {
                for (String cellData : row) {
                    Cell cell = new Cell().add(new Paragraph(cellData).setFontSize(10))
                            .setTextAlignment(TextAlignment.LEFT)
                            .setPadding(5);
                    table.addCell(cell);
                }
            }

            // Add table to document
            document.add(table);

            // Close document
            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Failure during document generation: {}", e.toString());
            return null;
        }
    }
}