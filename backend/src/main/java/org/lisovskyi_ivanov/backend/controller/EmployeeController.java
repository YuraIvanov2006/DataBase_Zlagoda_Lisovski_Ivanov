package org.lisovskyi_ivanov.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.request.EmployeeRequest;
import org.lisovskyi_ivanov.backend.dto.response.EmployeeDto;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.mapping.dto.EmployeeMapper;
import org.lisovskyi_ivanov.backend.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/employees")
// http://localhost:8080/api/v1/employees
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    // -- GET
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(
            employeeService.findAll()
                    .stream()
                    .map(employeeMapper::toDto)
                    .toList()
        );
    }

    @GetMapping("/ordered")
    public ResponseEntity<List<EmployeeDto>> getAllOrderBySurname() {
        return ResponseEntity.ok(
                employeeService.findAllOrderBySurname()
                        .stream()
                        .map(employeeMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/cashiers")
    public ResponseEntity<List<EmployeeDto>> getAllCashiers() {
        return ResponseEntity.ok(
                employeeService.findAllCashiers()
                        .stream()
                        .map(employeeMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/managers")
    public ResponseEntity<List<EmployeeDto>> getAllManagers() {
        return ResponseEntity.ok(
                employeeService.findAllManagers()
                        .stream()
                        .map(employeeMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(
                employeeMapper.toDto(employeeService.findById(id))
        );
    }

    @GetMapping("/search/surname")
    public ResponseEntity<EmployeeDto> getBySurname(@RequestParam String surname) {
        return ResponseEntity.ok(
                employeeMapper.toDto(employeeService.findBySurname(surname))
        );
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<EmployeeDto>> getAllByCity(@PathVariable String city) {
        return ResponseEntity.ok(
                employeeService.findAllByCity(city)
                        .stream()
                        .map(employeeMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/salary")
    public ResponseEntity<List<EmployeeDto>> getAllBySalaryGreaterThan(@RequestParam BigDecimal salary) {
        return ResponseEntity.ok(
                employeeService.findAllBySalaryGreaterThan(salary)
                        .stream()
                        .map(employeeMapper::toDto)
                        .toList()
        );
    }

    // -- POST
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        if (request.dateOfBirth().isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Employee must be at least 18 years old");
        }
        Employee saved = employeeService.save(employeeMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeMapper.toDto(saved));
    }

    // -- PUT
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        Employee employee = employeeMapper.toEntity(request);
        employee.setIdEmployee(id);
        return ResponseEntity.ok(employeeMapper.toDto(employeeService.update(employee)));
    }

    // -- DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

