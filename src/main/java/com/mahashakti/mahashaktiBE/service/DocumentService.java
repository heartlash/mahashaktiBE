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
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class DocumentService {

    public byte[] generateDocument(String name, String details, List<String> headers, List<List<String>> data,
                                   List<String> summaryHeaders, List<String> summaryData) {

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
            ImageData imageData = ImageDataFactory.create(resource.getInputStream().readAllBytes());
            Image logo = new Image(imageData).scaleToFit(60, 60)
                    .setFixedPosition(pdfDoc.getDefaultPageSize().getWidth() - 110, pdfDoc.getDefaultPageSize().getHeight() - 110);
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
                    .setMarginBottom(20);

            document.add(detail);

            if(!summaryHeaders.isEmpty()) {
                // Dynamic column widths for summary table
                float[] summaryColumnWidths = new float[summaryHeaders.size()];
                Arrays.fill(summaryColumnWidths, 100f / summaryHeaders.size()); // Auto-fill equal widths
                Table summaryTable = new Table(UnitValue.createPercentArray(summaryColumnWidths)).useAllAvailableWidth();

                // Add headers for summary table
                for (String header : summaryHeaders) {
                    Cell headerCell = new Cell().add(new Paragraph(header).setFont(font).setFontSize(12))
                            .setBackgroundColor(ColorConstants.YELLOW)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(5);
                    summaryTable.addHeaderCell(headerCell);
                }

                // Add data to the summary table
                for (String cellData : summaryData) {
                    Cell cell = new Cell().add(new Paragraph(cellData).setFontSize(10))
                            .setTextAlignment(TextAlignment.LEFT)
                            .setPadding(5);
                    summaryTable.addCell(cell);
                }

                // Add summary table to the document
                document.add(summaryTable);
            }

            // Add white space between the two tables
            document.add(new Paragraph().setMarginBottom(15)); // Creates space between tables

            // Dynamic column widths for main table
            float[] dataColumnWidths = new float[headers.size()];
            log.info("see data colum width: {}", dataColumnWidths);
            Arrays.fill(dataColumnWidths, 100f / headers.size()); // Auto-fill equal widths
            Table tableData = new Table(UnitValue.createPercentArray(dataColumnWidths)).useAllAvailableWidth();

            // Add headers for main table
            for (String header : headers) {
                Cell headerCell = new Cell().add(new Paragraph(header).setFont(font).setFontSize(12))
                        .setBackgroundColor(ColorConstants.YELLOW)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5);
                tableData.addHeaderCell(headerCell);
            }

            // Add data to the main table
            for (List<String> row : data) {
                for (String cellData : row) {
                    Cell cell = new Cell().add(new Paragraph(cellData).setFontSize(10))
                            .setTextAlignment(TextAlignment.LEFT)
                            .setPadding(5);
                    tableData.addCell(cell);
                }
            }

            // Add main table to the document
            document.add(tableData);

            // Close document
            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Failure during document generation: {}", e.toString());
            return null;
        }
    }
}