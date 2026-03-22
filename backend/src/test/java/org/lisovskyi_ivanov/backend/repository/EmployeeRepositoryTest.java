package org.lisovskyi_ivanov.backend.repository;

import org.junit.jupiter.api.Test;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.lisovskyi_ivanov.backend.mapping.mapper.EmployeeRowMapper;
import org.lisovskyi_ivanov.backend.repository.employee_repos.EmployeeRepository;
import org.lisovskyi_ivanov.backend.repository.employee_repos.EmployeeRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Import({EmployeeRepositoryImpl.class, EmployeeRowMapper.class})
class EmployeeRepositoryTest {
    @Autowired
    EmployeeRepository employeeRepository;

    @Test
    void findAll_shouldReturnAllEmployees_whenNoEmployees() {
        var employees = employeeRepository.findAll();
        assertTrue(employees.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllEmployees_whenExist() {
        employeeRepository.save(buildTestEmployee());

        var employees = employeeRepository.findAll();

        assertFalse(employees.isEmpty());
        assertEquals(1, employees.size());
    }

    @Test
    void findAllByRole_shouldReturnEmpty_whenNoEmployees() {
        var employees = employeeRepository.findAllByRole(Role.CASHIER);
        assertTrue(employees.isEmpty());
        var managers = employeeRepository.findAllByRole(Role.MANAGER);
        assertTrue(managers.isEmpty());
    }

    @Test
    void findAllByRole_shouldReturnAllEmployees_whenExist() {
        var testEmployee = buildTestEmployee();
        var currentRole = testEmployee.getEmplRole();
        employeeRepository.save(testEmployee);

        var employees = employeeRepository.findAllByRole(currentRole);
        assertFalse(employees.isEmpty());
        assertEquals(1, employees.size());
        assertEquals(currentRole, employees.getFirst().getEmplRole());
    }

    @Test
    void findAllCashiers_shouldReturnAllCashiers_whenExist() {
        var testEmployee = buildTestEmployee();
        testEmployee.setEmplRole(Role.CASHIER);
        employeeRepository.save(testEmployee);

        var cashiers = employeeRepository.findAllCashiers();
        assertFalse(cashiers.isEmpty());
        assertEquals(1, cashiers.size());
        assertEquals(Role.CASHIER, cashiers.getFirst().getEmplRole());
    }

    @Test
    void findAllCashiers_shouldReturnEmpty_whenOnlyManagers() {
        var testEmployee = buildTestEmployee();
        testEmployee.setEmplRole(Role.MANAGER);
        employeeRepository.save(testEmployee);

        var cashiers = employeeRepository.findAllCashiers();
        assertTrue(cashiers.isEmpty());
    }

    @Test
    void findAllManagers_shouldReturnAllManagers_whenExist() {
        var testEmployee = buildTestEmployee();
        testEmployee.setEmplRole(Role.MANAGER);
        employeeRepository.save(testEmployee);

        var employees = employeeRepository.findAllManagers();
        assertFalse(employees.isEmpty());
        assertEquals(1, employees.size());
        assertEquals(Role.MANAGER, employees.getFirst().getEmplRole());
    }

    @Test
    void findAllManagers_shouldReturnEmpty_whenOnlyCashiers() {
        var testEmployee = buildTestEmployee();
        testEmployee.setEmplRole(Role.CASHIER);
        employeeRepository.save(testEmployee);

        var managers = employeeRepository.findAllManagers();
        assertTrue(managers.isEmpty());
    }

    @Test
    void findAllByCity_shouldReturnEmpty_whenNoEmployees() {
        var testCity = "Kyiv";
        var employees = employeeRepository.findAllByCity(testCity);
        assertTrue(employees.isEmpty());
    }

    @Test
    void findAllByCity_shouldReturnAllEmployees_whenExist() {
        var testEmployee = buildTestEmployee();
        var testEmployee2 = buildTestEmployee();
        var currentCity = testEmployee.getEmplCity();
        employeeRepository.save(testEmployee);
        employeeRepository.save(testEmployee2);

        var employees = employeeRepository.findAllByCity(currentCity);
        assertFalse(employees.isEmpty());
        assertEquals(2, employees.size());
        assertEquals(currentCity, employees.getFirst().getEmplCity());
        assertEquals(currentCity, employees.getLast().getEmplCity());
    }

    @Test
    void findAllOrderBySurname_shouldReturnAllEmployees_whenExist() {
        var testEmployee = buildTestEmployee();
        var testEmployee2 = buildTestEmployee();
        testEmployee2.setEmplSurname("Bondarenko");

        employeeRepository.save(testEmployee);
        employeeRepository.save(testEmployee2);

        var employees = employeeRepository.findAllOrderBySurname();
        assertFalse(employees.isEmpty());
        assertEquals(2, employees.size());
        assertEquals(0, testEmployee2.getEmplSurname().compareTo(employees.getFirst().getEmplSurname()));
        assertTrue(employees.getFirst().getEmplSurname().compareTo(employees.getLast().getEmplSurname()) < 0);
    }

    @Test
    void findById_shouldReturnEmployee_whenExists() {
        var savedEmployee = employeeRepository.save(buildTestEmployee());

        var employee = employeeRepository.findById(savedEmployee.getIdEmployee());
        assertTrue(employee.isPresent());
        assertEquals(savedEmployee.getIdEmployee(), employee.get().getIdEmployee());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        var testId = ThreadLocalRandom.current().nextLong(1, 1000000);
        var employee = employeeRepository.findById(testId);
        assertTrue(employee.isEmpty());
    }

    @Test
    void findBySurname_shouldReturnEmployee_whenExists() {
        var testEmployee = buildTestEmployee();
        var testSurname = testEmployee.getEmplSurname();
        employeeRepository.save(testEmployee);

        var employee = employeeRepository.findBySurname(testSurname);
        assertTrue(employee.isPresent());
        assertEquals(testSurname, employee.get().getEmplSurname());
    }

    @Test
    void save_shouldReturnEmployeeWithId_whenSaved() {
        var testEmployee = buildTestEmployee();
        var savedEmployee = employeeRepository.save(testEmployee);

        assertNotNull(savedEmployee.getIdEmployee());
        assertEquals(testEmployee.getEmplSurname(), savedEmployee.getEmplSurname());
    }

    @Test
    void update_shouldUpdateEmployee_whenExists() {
            var testEmployee = buildTestEmployee();
            var savedEmployee = employeeRepository.save(testEmployee);

            savedEmployee.setEmplCity("Lviv");
            employeeRepository.update(savedEmployee);

            var updatedEmployee = employeeRepository.findById(savedEmployee.getIdEmployee());
            assertTrue(updatedEmployee.isPresent());
            assertEquals("Lviv", updatedEmployee.get().getEmplCity());
    }

    @Test
    void deleteById_shouldRemoveEmployee_whenExists() {
        var testEmployee = buildTestEmployee();
        var savedEmployee = employeeRepository.save(testEmployee);
        var testId = savedEmployee.getIdEmployee();

        employeeRepository.deleteById(testId);

        var deletedEmployee = employeeRepository.findById(testId);
        assertTrue(deletedEmployee.isEmpty());
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        var testEmployee = buildTestEmployee();
        var savedEmployee = employeeRepository.save(testEmployee);
        var testId = savedEmployee.getIdEmployee();

        var exists = employeeRepository.existsById(testId);
        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        var testId = ThreadLocalRandom.current().nextLong(1, 1000000);

        var exists = employeeRepository.existsById(testId);
        assertFalse(exists);
    }


    // utility methods
    Employee buildTestEmployee() {
        return Employee.builder()
                .emplSurname("Shevchenko")
                .emplName("Taras")
                .emplPatronymic("Hryhorovych")
                .emplRole(Role.CASHIER)
                .salary(new BigDecimal("15000.00"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .dateOfStart(LocalDate.of(2020, 1, 1))
                .emplPhoneNumber("+380501234567")
                .emplCity("Kyiv")
                .emplStreet("Khreshchatyk")
                .emplZipCode("01001")
                .build();
    }
}
