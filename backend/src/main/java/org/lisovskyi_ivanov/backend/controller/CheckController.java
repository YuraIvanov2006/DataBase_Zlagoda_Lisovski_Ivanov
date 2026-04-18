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
}