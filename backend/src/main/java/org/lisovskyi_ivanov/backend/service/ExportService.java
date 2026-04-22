package org.lisovskyi_ivanov.backend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.lisovskyi_ivanov.backend.exception.ExportException;
import org.openpdf.text.*;
import org.openpdf.text.Font;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    public byte[] exportToPdf(
            List<String> headers,
            List<List<String>> rows,
            String title
    ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, baos);

            BaseFont bf;
            try (var is = getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf")) {
                if (is == null) throw new RuntimeException("Font file not found");
                byte[] fontBytes = is.readAllBytes();
                bf = BaseFont.createFont("Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
            }

            Font titleFont = new Font(bf, 18, Font.BOLD);
            Font headerFont = new Font(bf, 14, Font.BOLD);
            Font rowFont = new Font(bf, 12);

            org.openpdf.text.HeaderFooter header = new org.openpdf.text.HeaderFooter(new Phrase("Автоматизована інформаційна система ZLAGODA", headerFont), false);
            header.setAlignment(Element.ALIGN_CENTER);
            doc.setHeader(header);

            org.openpdf.text.HeaderFooter footer = new org.openpdf.text.HeaderFooter(new Phrase("Сторінка "), new Phrase("."));
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.setFooter(footer);

            doc.open();
            doc.add(new Paragraph(title, titleFont));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(100f);
            for (String headerName : headers) {
                var cell = new PdfPCell(new Phrase(headerName, headerFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            for (List<String> row : rows) {
                for (String cellValue : row) {
                    table.addCell(new Phrase(cellValue, rowFont));
                }
            }

            doc.add(table);
        } catch (DocumentException | IOException e) {
            throw new ExportException("PDF", e);
        } finally {
            if (doc.isOpen()) doc.close();
        }

        return baos.toByteArray();
    }

    public byte[] exportToExcel(
            List<String> headers,
            List<List<String>> rows,
            String sheetName
    ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);

            var header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            var headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.size(); i++) {
                var cell = header.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < rows.size(); i++) {
                var row = sheet.createRow(i + 1);
                List<String> rowData = rows.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    row.createCell(j).setCellValue(rowData.get(j));
                }
            }

            workbook.write(baos);
        } catch (IOException e) {
            throw new ExportException("Excel", e);
        }

        return baos.toByteArray();
    }
}
