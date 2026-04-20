package org.lisovskyi_ivanov.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.request.CheckRequest;
import org.lisovskyi_ivanov.backend.dto.response.CheckDto;
import org.lisovskyi_ivanov.backend.entity.Check;
import org.lisovskyi_ivanov.backend.entity.Sale;
import org.lisovskyi_ivanov.backend.mapping.dto.CheckMapper;
import org.lisovskyi_ivanov.backend.mapping.dto.SaleMapper;
import org.lisovskyi_ivanov.backend.service.CheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checks")
@RequiredArgsConstructor
public class CheckController {
    private final CheckService checkService;
    private final CheckMapper checkMapper;
    private final SaleMapper saleMapper;

    @GetMapping
    public ResponseEntity<List<CheckDto>> getAllChecks() {
        List<CheckDto> checks = checkService.findAll().stream()
                .map(checkMapper::toDto)
                .toList();
        return ResponseEntity.ok(checks);
    }

    @GetMapping("/{checkNumber}")
    public ResponseEntity<CheckDto> getCheckById(@PathVariable String checkNumber) {
        return ResponseEntity.ok(checkMapper.toDto(
                checkService.findByCheckNumber(checkNumber)));
    }

    @PostMapping
    public ResponseEntity<CheckDto> createCheck(@Valid @RequestBody CheckRequest request) {
        Check checkToSave = checkMapper.toEntity(request);
        List<Sale> sales = request.sales().stream()
                .map(saleMapper::toEntity)
                .toList();
        Check savedCheck = checkService.save(checkToSave, sales);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checkMapper.toDto(savedCheck));
    }

    @DeleteMapping("/{checkNumber}")
    public ResponseEntity<Void> deleteCheck(@PathVariable String checkNumber) {
        checkService.deleteByCheckNumber(checkNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CheckDto>> getFilteredChecks(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime from,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime to) {
        
        List<CheckDto> checks;
        if (employeeId != null && from != null && to != null) {
            checks = checkService.findByEmployeeIdAndPrintDateBetween(employeeId, from, to).stream()
                    .map(checkMapper::toDto).toList();
        } else if (from != null && to != null) {
            checks = checkService.findByPrintDateBetween(from, to).stream()
                    .map(checkMapper::toDto).toList();
        } else if (employeeId != null) {
            checks = checkService.findAllByEmployeeId(employeeId).stream()
                    .map(checkMapper::toDto).toList();
        } else {
            checks = checkService.findAll().stream()
                    .map(checkMapper::toDto).toList();
        }
        return ResponseEntity.ok(checks);
    }

    @GetMapping("/sum")
    public ResponseEntity<java.math.BigDecimal> getTotalSum(
            @RequestParam(required = false) Long employeeId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime from,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime to) {
        
        java.math.BigDecimal sum;
        if (employeeId != null) {
            sum = checkService.calculateTotalSumByEmployeeAndPeriod(employeeId, from, to);
        } else {
            sum = checkService.calculateTotalSumByPeriod(from, to);
        }
        return ResponseEntity.ok(sum != null ? sum : java.math.BigDecimal.ZERO);
    }
}