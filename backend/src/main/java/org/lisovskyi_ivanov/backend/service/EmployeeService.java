package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.lisovskyi_ivanov.backend.exception.NotFoundException;
import org.lisovskyi_ivanov.backend.repository.employee_repos.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
    public Employee findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(Employee.class, "id", id));
    }

    @Transactional(readOnly = true)
    public Employee findBySurname(String surname) {
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname must not be null or blank");
        }
        return repository.findBySurname(surname)
                .orElseThrow(() -> new NotFoundException(Employee.class, "surname", surname));
    }

    @Transactional(readOnly = true)
    public Employee findBySurnameAndName(String surname, String name) {
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        return repository.findBySurnameAndName(surname, name)
                .orElseThrow(() -> new NotFoundException(Employee.class,
                        List.of("surname", "name"),
                        List.of(surname, name))
                );
    }

    @Transactional(readOnly = true)
    public Employee findBySurnameAndNameAndPatronymic(String surname, String name, String patronymic) {
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        if (patronymic == null || patronymic.isBlank()) {
            throw new IllegalArgumentException("Patronymic must not be null or blank");
        }
        return repository.findBySurnameAndNameAndPatronymic(surname, name, patronymic)
                .orElseThrow(() -> new NotFoundException(Employee.class,
                        List.of("surname", "name", "patronymic"),
                        List.of(surname, name, patronymic))
                );
    }

    @Transactional
    public Employee save(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }
        return repository.save(employee);
    }

    @Transactional
    public Employee update(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }
        int rows = repository.update(employee);
        if (rows == 0) {
            throw new NotFoundException(Employee.class, "id", employee.getIdEmployee());
        }
        return employee;
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
        int rows = repository.deleteById(id);
        if (rows == 0) {
            throw new NotFoundException(Employee.class, "id", id);
        }
    }

    @Transactional
    public void delete(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }
        int rows = repository.delete(employee);
        if (rows == 0) {
            throw new NotFoundException(Employee.class, "id", employee.getIdEmployee());
        }
    }
}

