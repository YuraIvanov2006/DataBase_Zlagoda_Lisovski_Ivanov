package org.lisovskyi_ivanov.backend.repository.employee_repos;

import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    List<Employee> findAll();
    List<Employee> findAllByRole(Role role);
    List<Employee> findAllCashiers();
    List<Employee> findAllManagers();
    List<Employee> findAllByCity(String city);
    List<Employee> findAllOrderBySurname();
    List<Employee> findAllBySalaryGreaterThan(BigDecimal salary);
    Optional<Employee> findById(Long id);
    Optional<Employee> findBySurname(String surname);
    Optional<Employee> findBySurnameAndName(String surname, String name);
    Optional<Employee> findBySurnameAndNameAndPatronymic(String surname, String name, String patronymic);
    Employee save(Employee employee);
    void update(Employee employee);
    boolean existsById(Long id);
    void deleteById(Long id);
    void delete(Employee employee);
}
