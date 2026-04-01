package org.lisovskyi_ivanov.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lisovskyi_ivanov.backend.entity.Check;
import org.lisovskyi_ivanov.backend.entity.CustomerCard;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.lisovskyi_ivanov.backend.mapping.mapper.CheckRowMapper;
import org.lisovskyi_ivanov.backend.mapping.mapper.CustomerCardRowMapper;
import org.lisovskyi_ivanov.backend.mapping.mapper.EmployeeRowMapper;
import org.lisovskyi_ivanov.backend.repository.check_repos.CheckRepository;
import org.lisovskyi_ivanov.backend.repository.check_repos.CheckRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.customer_card_repos.CustomerCardRepository;
import org.lisovskyi_ivanov.backend.repository.customer_card_repos.CustomerCardRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.employee_repos.EmployeeRepository;
import org.lisovskyi_ivanov.backend.repository.employee_repos.EmployeeRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Import({
        CheckRepositoryImpl.class, CheckRowMapper.class,
        EmployeeRepositoryImpl.class, EmployeeRowMapper.class,
        CustomerCardRepositoryImpl.class, CustomerCardRowMapper.class
})
class CheckRepositoryTest {

    @Autowired
    CheckRepository checkRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CustomerCardRepository customerCardRepository;

    private Employee savedEmployee;
    private CustomerCard savedCustomerCard;

    @BeforeEach
    void setUp() {
        savedEmployee = employeeRepository.save(buildTestEmployee());
        savedCustomerCard = customerCardRepository.save(buildTestCustomerCard());
    }

    @Test
    void findAll_shouldReturnEmpty_whenNoChecks() {
        var checks = checkRepository.findAll();
        assertTrue(checks.isEmpty());
    }

    @Test
    void findByCheckNumber_shouldReturnCheck_whenExists() {
        var testCheck = buildTestCheck();
        checkRepository.save(testCheck);

        var check = checkRepository.findByCheckNumber(testCheck.getCheckNumber());

        assertTrue(check.isPresent());
        assertEquals(testCheck.getCheckNumber(), check.get().getCheckNumber());
    }

    @Test
    void findByCheckNumber_shouldReturnEmpty_whenNotExists() {
        var check = checkRepository.findByCheckNumber("NON_EXISTING");
        assertTrue(check.isEmpty());
    }

    @Test
    void findByEmployeeId_shouldReturnChecks_whenExist() {
        var testCheck = buildTestCheck();
        checkRepository.save(testCheck);

        var checks = checkRepository.findByEmployeeId(savedEmployee.getIdEmployee());

        assertFalse(checks.isEmpty());
        assertEquals(testCheck.getCheckNumber(), checks.getFirst().getCheckNumber());
    }

    @Test
    void findByCustomerCardNumber_shouldReturnChecks_whenExist() {
        var testCheck = buildTestCheck();
        checkRepository.save(testCheck);

        var checks = checkRepository.findByCustomerCardNumber(savedCustomerCard.getCardNumber());

        assertFalse(checks.isEmpty());
        assertEquals(testCheck.getCheckNumber(), checks.getFirst().getCheckNumber());
    }

    @Test
    void findByPrintDate_shouldReturnChecks_whenExist() {
        var testCheck = buildTestCheck();
        checkRepository.save(testCheck);

        var checks = checkRepository.findByPrintDate(testCheck.getPrintDate());

        assertFalse(checks.isEmpty());
    }

    @Test
    void findBySumTotalGreaterThan_shouldReturnChecks_whenExist() {
        var testCheck = buildTestCheck();
        testCheck.setSumTotal(new BigDecimal("500.00"));
        checkRepository.save(testCheck);

        var checks = checkRepository.findBySumTotalGreaterThan(new BigDecimal("400.00"));

        assertFalse(checks.isEmpty());
    }

    @Test
    void findByVatGreaterThan_shouldReturnChecks_whenExist() {
        var testCheck = buildTestCheck();
        testCheck.setVat(new BigDecimal("100.00"));
        checkRepository.save(testCheck);

        var checks = checkRepository.findByVatGreaterThan(new BigDecimal("50.00"));

        assertFalse(checks.isEmpty());
    }

    @Test
    void existsByCheckNumber_shouldReturnTrue_whenExists() {
        var testCheck = buildTestCheck();
        checkRepository.save(testCheck);

        var exists = checkRepository.existsByCheckNumber(testCheck.getCheckNumber());
        assertTrue(exists);
    }

    @Test
    void existsByCheckNumber_shouldReturnFalse_whenNotExists() {
        var exists = checkRepository.existsByCheckNumber("NON_EXISTING");
        assertFalse(exists);
    }

    @Test
    void save_shouldReturnSavedCheck() {
        var testCheck = buildTestCheck();
        var savedCheck = checkRepository.save(testCheck);

        assertNotNull(savedCheck);
        assertEquals(testCheck.getCheckNumber(), savedCheck.getCheckNumber());
    }

    @Test
    void update_shouldUpdateCheck_whenExists() {
        var testCheck = buildTestCheck();
        checkRepository.save(testCheck);

        testCheck.setSumTotal(new BigDecimal("999.99"));
        checkRepository.update(testCheck);

        var updatedCheck = checkRepository.findByCheckNumber(testCheck.getCheckNumber());

        assertTrue(updatedCheck.isPresent());

        assertEquals(0, new BigDecimal("999.99").compareTo(updatedCheck.get().getSumTotal()));
    }

    @Test
    void deleteByCheckNumber_shouldRemoveCheck_whenExists() {
        var testCheck = buildTestCheck();
        checkRepository.save(testCheck);

        checkRepository.deleteByCheckNumber(testCheck.getCheckNumber());

        var deletedCheck = checkRepository.findByCheckNumber(testCheck.getCheckNumber());
        assertTrue(deletedCheck.isEmpty());
    }

    @Test
    void delete_shouldRemoveCheck_whenExists() {
        var testCheck = buildTestCheck();
        checkRepository.save(testCheck);

        checkRepository.delete(testCheck);

        var deletedCheck = checkRepository.findByCheckNumber(testCheck.getCheckNumber());
        assertTrue(deletedCheck.isEmpty());
    }

    Check buildTestCheck() {
        return Check.builder()
                .checkNumber(UUID.randomUUID().toString().substring(0, 10))
                .employee(savedEmployee)
                .customerCard(savedCustomerCard)
                .printDate(LocalDateTime.now())
                .sumTotal(new BigDecimal("250.50"))
                .vat(new BigDecimal("41.75"))
                .build();
    }

    Employee buildTestEmployee() {
        return Employee.builder()
                .emplSurname("TestSurname")
                .emplName("TestName")
                .emplPatronymic("TestPatronymic")
                .emplRole(Role.CASHIER)
                .salary(new BigDecimal("15000.00"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .dateOfStart(LocalDate.of(2020, 1, 1))
                .emplPhoneNumber("+380501234567")
                .emplCity("Kyiv")
                .emplStreet("TestStreet")
                .emplZipCode("01001")
                .build();
    }

    CustomerCard buildTestCustomerCard() {
        return CustomerCard.builder()
                .cardNumber(UUID.randomUUID().toString().substring(0, 13))
                .custSurname("CardSurname")
                .custName("CardName")
                .custPatronymic("CardPatronymic")
                .custPhoneNumber("+380601234567")
                .custCity("Kyiv")
                .custStreet("CardStreet")
                .custZipCode("02002")
                .percent(5)
                .build();
    }
}