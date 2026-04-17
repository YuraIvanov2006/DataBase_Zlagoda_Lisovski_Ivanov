package org.lisovskyi_ivanov.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.request.CheckRequest;
import org.lisovskyi_ivanov.backend.dto.response.CheckDto;
import org.lisovskyi_ivanov.backend.entity.Check;
import org.lisovskyi_ivanov.backend.mapping.dto.CheckMapper;
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

    // -- GET
    @GetMapping
    public ResponseEntity<List<CheckDto>> getAllChecks() {
        List<CheckDto> checks = checkService.findAll().stream()
                .map(checkMapper::toDto)
                .toList();
        return ResponseEntity.ok(checks);
    }

    @GetMapping("/{checkNumber}")
    public ResponseEntity<CheckDto> getCheckById(@PathVariable String checkNumber) {
        Check check = checkService.findByCheckNumber(checkNumber);
        return ResponseEntity.ok(checkMapper.toDto(check));
    }

    // -- POST
    @PostMapping
    public ResponseEntity<CheckDto> createCheck(@Valid @RequestBody CheckRequest request) {
        Check checkToSave = checkMapper.toEntity(request);
        Check savedCheck = checkService.save(checkToSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(checkMapper.toDto(savedCheck));
    }

    // -- PUT
    @PutMapping("/{checkNumber}")
    public ResponseEntity<CheckDto> updateCheck(
            @PathVariable String checkNumber,
            @Valid @RequestBody CheckRequest request) {

        Check checkToUpdate = checkMapper.toEntity(request);
        checkToUpdate.setCheckNumber(checkNumber); // Захист від перезапису іншого чеку

        Check updatedCheck = checkService.update(checkToUpdate);
        return ResponseEntity.ok(checkMapper.toDto(updatedCheck));
    }

    // -- DELETE
    @DeleteMapping("/{checkNumber}")
    public ResponseEntity<Void> deleteCheck(@PathVariable String checkNumber) {
        checkService.deleteByCheckNumber(checkNumber);
        return ResponseEntity.noContent().build();
    }
}