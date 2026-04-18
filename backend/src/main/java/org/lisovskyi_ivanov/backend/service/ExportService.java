package org.lisovskyi_ivanov.backend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.lisovskyi_ivanov.backend.exception.ExportException;
import org.openpdf.text.*;
import org.openpdf.text.Font;
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

            Font titleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD);
            Font headerFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD);
            Font rowFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12);

            doc.open();
            doc.add(new Paragraph(title, titleFont));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(headers.size());
            for (String header : headers) {
                var cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            for (List<String> row : rows) {
                for (String cellValue : row) {
                    table.addCell(new Phrase(cellValue, rowFont));
                }
            }

            doc.add(table);
        } catch (DocumentException e) {
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
