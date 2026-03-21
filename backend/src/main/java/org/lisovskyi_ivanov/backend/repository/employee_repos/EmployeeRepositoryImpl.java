package org.lisovskyi_ivanov.backend.repository.employee_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.lisovskyi_ivanov.backend.mapping.mapper.EmployeeRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private static final String TABLE_NAME = "employees";
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

    private final JdbcTemplate jdbc;
    private final EmployeeRowMapper employeeRowMapper;

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employees";
        return jdbc.query(sql, employeeRowMapper);
    }

    @Override
    public List<Employee> findAllByRole(Role role) {
        String sql = SELECT_ALL + " WHERE empl_role = ?";
        return jdbc.query(sql, employeeRowMapper, role.getRoleName());
    }

    @Override
    public List<Employee> findAllCashiers() {
        String sql = SELECT_ALL + " WHERE empl_role = 'cashier'";
        return jdbc.query(sql, employeeRowMapper);
    }

    @Override
    public List<Employee> findAllManagers() {
        String sql = SELECT_ALL + " WHERE empl_role = 'manager'";
        return jdbc.query(sql, employeeRowMapper);
    }

    @Override
    public List<Employee> findAllByCity(String city) {
        String sql = SELECT_ALL + " WHERE empl_city = ?";
        return jdbc.query(sql, employeeRowMapper, city);
    }

    @Override
    public List<Employee> findAllOrderBySurname() {
        String sql = SELECT_ALL + " ORDER BY empl_surname";
        return jdbc.query(sql, employeeRowMapper);
    }

    @Override
    public List<Employee> findAllBySalaryGreaterThan(BigDecimal salary) {
        String sql = SELECT_ALL + " WHERE salary > ?";
        return jdbc.query(sql, employeeRowMapper, salary);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        String sql = SELECT_ALL + " WHERE id_employee = ?";
        try {
            var employee = jdbc.queryForObject(sql, employeeRowMapper, id);
            return Optional.of(employee);
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Employee> findBySurname(String surname) {
        String sql = SELECT_ALL + " WHERE empl_surname = ?";
        try {
            var employee = jdbc.queryForObject(sql, employeeRowMapper, surname);
            return Optional.of(employee);
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Employee> findBySurnameAndName(String surname, String name) {
        String sql = SELECT_ALL + " WHERE empl_surname = ? AND empl_name = ?";
        try {
            var employee = jdbc.queryForObject(sql, employeeRowMapper, surname, name);
            return Optional.of(employee);
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Employee> findBySurnameAndNameAndPatronymic(String surname, String name, String patronymic) {
        String sql = SELECT_ALL + " WHERE empl_surname = ? AND empl_name = ? AND empl_patronymic = ?";
        try {
            var employee = jdbc.queryForObject(sql, employeeRowMapper, surname, name, patronymic);
            return Optional.of(employee);
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    @Override
    public Employee save(Employee employee) {
        String sql =
        """
        INSERT INTO employees (
            id_employee, empl_surname, empl_name, empl_patronymic,\s
            empl_role, salary, date_of_birth, date_of_start,\s
            empl_phone_number, empl_city, empl_street, empl_zip_code
        ) VALUES (
            :idEmployee, :emplSurname, :emplName, :emplPatronymic,\s
            :emplRole, :salary, :dateOfBirth, :dateOfStart,\s
            :phoneNumber, :city, :street, :zipCode
        ) RETURNING *;
        """;

        return jdbc.queryForObject(sql, employeeRowMapper, employee);
    }

    @Override
    public void update(Employee employee) {
        String sql =
        """
        UPDATE employees SET
            empl_surname = :emplSurname,
            empl_name = :emplName,
            empl_patronymic = :emplPatronymic,
            empl_role = :emplRole,
            salary = :salary,
            date_of_birth = :dateOfBirth,
            date_of_start = :dateOfStart,
            empl_phone_number = :phoneNumber,
            empl_city = :city,
            empl_street = :street,
            empl_zip_code = :zipCode
        WHERE id_employee = :idEmployee;
        """;

        jdbc.update(sql, employee);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM employees WHERE id_employee = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM employees WHERE id_employee = ?";
        jdbc.update(sql, id);
    }

    @Override
    public void delete(Employee employee) {
        deleteById(employee.getIdEmployee());
    }
}
