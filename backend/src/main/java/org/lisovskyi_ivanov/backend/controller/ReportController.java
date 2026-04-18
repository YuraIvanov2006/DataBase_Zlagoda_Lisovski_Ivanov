package org.lisovskyi_ivanov.backend.controller;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.enums.ExportEntities;
import org.lisovskyi_ivanov.backend.service.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ExportService exportService;
    private final EmployeeService employeeService;
    private final CustomerCardService customerCardService;
    private final ProductService productService;
    private final StoreProductService storeProductService;
    private final CheckService checkService;

    //
    // PDF
    //
    @GetMapping("/{entityName}/pdf")
    public ResponseEntity<byte[]> generatePdfReport(@PathVariable String entityName) {
        var entity = ExportEntities.getEntityFromString(entityName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid entity: " + entityName));

        byte[] pdf = exportService.exportToPdf(
                resolveHeaders(entity),
                resolveRows(entity),
                entity + " Report"
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + entity + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    //
    // Excel
    //
    @GetMapping("/{entityName}/excel")
    public ResponseEntity<byte[]> generateExcelReport(@PathVariable String entityName) {
        String entity = ExportEntities.getEntityFromString(entityName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid entity: " + entityName));

        byte[] excel = exportService.exportToExcel(
                resolveHeaders(entity),
                resolveRows(entity),
                entity
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + entity + ".xlsx") // ✅ entity
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }


    // private methods
    private List<String> resolveHeaders(String entity) {
        return switch (entity) {
            case "employees" -> List.of(
                    "ID", "Surname", "Name", "Patronymic",
                    "Role", "Salary", "Phone", "City");
            case "customers" -> List.of(
                    "Card Number", "Surname", "Name", "Patronymic",
                    "Phone", "City", "Discount (%)");
            case "products" -> List.of(
                    "ID", "Name", "Category", "Characteristics");
            case "store-products" -> List.of(
                    "UPC", "Name", "Price", "Quantity", "Promotional");
            case "checks" -> List.of(
                    "Check Number", "Employee ID", "Print Date",
                    "Sum Total", "VAT");
            default -> throw new IllegalArgumentException("Unknown entity: " + entity);
        };
    }

    private List<List<String>> resolveRows(String entity) {
        return switch (entity) {
            case "employees" -> employeeService.findAll().stream()
                    .map(e -> List.of(
                            String.valueOf(e.getIdEmployee()),
                            e.getEmplSurname(),
                            e.getEmplName(),
                            e.getEmplPatronymic() != null ? e.getEmplPatronymic() : "",
                            e.getEmplRole().name(),
                            e.getSalary().toString(),
                            e.getEmplPhoneNumber(),
                            e.getEmplCity() != null ? e.getEmplCity() : ""
                    )).toList();

            case "customers" -> customerCardService.findAll().stream()
                    .map(c -> List.of(
                            c.getCardNumber(),
                            c.getCustSurname(),
                            c.getCustName(),
                            c.getCustPatronymic() != null ? c.getCustPatronymic() : "",
                            c.getCustPhoneNumber(),
                            c.getCustCity() != null ? c.getCustCity() : "",
                            String.valueOf(c.getPercent())
                    )).toList();

            case "products" -> productService.findAll().stream()
                    .map(p -> List.of(
                            String.valueOf(p.getIdProduct()),
                            p.getProductName(),
                            p.getCategory() != null ? p.getCategory().getCategoryName() : "",
                            p.getCharacteristics() != null ? p.getCharacteristics() : ""
                    )).toList();

            case "store-products" -> storeProductService.findAll().stream()
                    .map(sp -> List.of(
                            sp.getUpc(),
                            sp.getProduct() != null ? sp.getProduct().getProductName() : "",
                            sp.getSellingPrice().toString(),
                            String.valueOf(sp.getProductsNumber()),
                            sp.isPromotionalProduct() ? "Yes" : "No"
                    )).toList();

            case "checks" -> checkService.findAll().stream()
                    .map(ch -> List.of(
                            ch.getCheckNumber(),
                            ch.getEmployee() != null
                                    ? String.valueOf(ch.getEmployee().getIdEmployee()) : "",
                            ch.getPrintDate() != null ? ch.getPrintDate().toString() : "",
                            ch.getSumTotal() != null ? ch.getSumTotal().toString() : "",
                            ch.getVat() != null ? ch.getVat().toString() : ""
                    )).toList();

            default -> throw new IllegalArgumentException("Unknown entity: " + entity);
        };
    }
}
