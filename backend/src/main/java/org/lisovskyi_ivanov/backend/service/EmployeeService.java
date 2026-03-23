package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.lisovskyi_ivanov.backend.repository.employee_repos.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Employee> findAllByRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role must not be null");
        }
        return repository.findAllByRole(role);
    }

    @Transactional(readOnly = true)
    public List<Employee> findAllCashiers() {
        return repository.findAllCashiers();
    }

    @Transactional(readOnly = true)
    public List<Employee> findAllManagers() {
        return repository.findAllManagers();
    }

    @Transactional(readOnly = true)
    public List<Employee> findAllByCity(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City must not be null or blank");
        }
        return repository.findAllByCity(city);
    }

    @Transactional(readOnly = true)
    public List<Employee> findAllOrderBySurname() {
        return repository.findAllOrderBySurname();
    }

    @Transactional(readOnly = true)
    public List<Employee> findAllBySalaryGreaterThan(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salary must not be null or negative");
        }
        return repository.findAllBySalaryGreaterThan(salary);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> findBySurname(String surname) {
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname must not be null or blank");
        }
        return repository.findBySurname(surname);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> findBySurnameAndName(String surname, String name) {
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        return repository.findBySurnameAndName(surname, name);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> findBySurnameAndNameAndPatronymic(String surname, String name, String patronymic) {
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        if (patronymic == null || patronymic.isBlank()) {
            throw new IllegalArgumentException("Patronymic must not be null or blank");
        }
        return repository.findBySurnameAndNameAndPatronymic(surname, name, patronymic);
    }

    @Transactional
    public Employee save(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }
        return repository.save(employee);
    }

    @Transactional
    public void update(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }
        int updated = repository.update(employee);
        if (updated == 0) {
            throw new IllegalArgumentException("Employee with id " + employee.getIdEmployee() + " not found");
        }
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        return repository.existsById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        int deleted = repository.deleteById(id);
        if (deleted == 0) {
            throw new IllegalArgumentException("Employee with id " + id + " not found");
        }
    }

    @Transactional
    public void delete(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }
        int deleted = repository.delete(employee);
        if (deleted == 0) {
            throw new IllegalArgumentException("Employee with id " + employee.getIdEmployee() + " not found");
        }
    }
}
