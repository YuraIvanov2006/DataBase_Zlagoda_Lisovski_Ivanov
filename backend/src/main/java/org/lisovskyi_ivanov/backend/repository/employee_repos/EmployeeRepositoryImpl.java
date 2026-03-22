package org.lisovskyi_ivanov.backend.repository.employee_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.lisovskyi_ivanov.backend.mapping.mapper.EmployeeRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
    private final NamedParameterJdbcTemplate namedJdbc;
    private final EmployeeRowMapper employeeRowMapper;

    @Override
    public List<Employee> findAll() {
        return jdbc.query(SELECT_ALL, employeeRowMapper);
    }

    @Override
    public List<Employee> findAllByRole(Role role) {
        String sql = SELECT_ALL + " WHERE empl_role = ?";
        return jdbc.query(sql, employeeRowMapper, role.getRoleName());
    }

    @Override
    public List<Employee> findAllCashiers() {
        return findAllByRole(Role.CASHIER);
    }

    @Override
    public List<Employee> findAllManagers() {
        return findAllByRole(Role.MANAGER);
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
        return jdbc.query(sql, employeeRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Employee> findBySurname(String surname) {
        String sql = SELECT_ALL + " WHERE empl_surname = ?";
        return jdbc.query(sql, employeeRowMapper, surname)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Employee> findBySurnameAndName(String surname, String name) {
        String sql = SELECT_ALL + " WHERE empl_surname = ? AND empl_name = ?";
        return jdbc.query(sql, employeeRowMapper, surname, name)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Employee> findBySurnameAndNameAndPatronymic(String surname, String name, String patronymic) {
        String sql = SELECT_ALL + " WHERE empl_surname = ? AND empl_name = ? AND empl_patronymic = ?";
        return jdbc.query(sql, employeeRowMapper, surname, name, patronymic)
                .stream()
                .findFirst();
    }

    @Override
    public Employee save(Employee employee) {
        String sql =
        """
        INSERT INTO employees (
            empl_surname, empl_name, empl_patronymic,
            empl_role, salary, date_of_birth, date_of_start,
            empl_phone_number, empl_city, empl_street, empl_zip_code
        ) VALUES (
            :emplSurname, :emplName, :emplPatronymic,
            :emplRole, :salary, :dateOfBirth, :dateOfStart,
            :emplPhoneNumber, :emplCity, :emplStreet, :emplZipCode
        );
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(sql, employeeParameters(employee), keyHolder, new String[] {"id_employee"});

        Long generatedId = keyHolder.getKeyAs(Long.class);
        return findById(generatedId).orElseThrow();
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
            empl_phone_number = :emplPhoneNumber,
            empl_city = :emplCity,
            empl_street = :emplStreet,
            empl_zip_code = :emplZipCode
        WHERE id_employee = :idEmployee;
        """;

        namedJdbc.update(sql, employeeParameters(employee));
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


    private SqlParameterSource employeeParameters(Employee employee) {
        return new MapSqlParameterSource()
                .addValue("idEmployee", employee.getIdEmployee())
                .addValue("emplSurname", employee.getEmplSurname())
                .addValue("emplName", employee.getEmplName())
                .addValue("emplPatronymic", employee.getEmplPatronymic())
                .addValue("emplRole", employee.getEmplRole().getRoleName())
                .addValue("salary", employee.getSalary())
                .addValue("dateOfBirth", employee.getDateOfBirth())
                .addValue("dateOfStart", employee.getDateOfStart())
                .addValue("emplPhoneNumber", employee.getEmplPhoneNumber())
                .addValue("emplCity", employee.getEmplCity())
                .addValue("emplStreet", employee.getEmplStreet())
                .addValue("emplZipCode", employee.getEmplZipCode());
    }
}
