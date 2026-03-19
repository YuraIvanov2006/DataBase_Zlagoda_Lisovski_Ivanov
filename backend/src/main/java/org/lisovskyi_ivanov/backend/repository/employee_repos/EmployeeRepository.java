package org.lisovskyi_ivanov.backend.repository.employee_repos;

import org.lisovskyi_ivanov.backend.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    List<Employee> findAll();
    Optional<Employee> findById(Long id);
    Optional<Employee> findBySurname(String surname);
    Optional<Employee> findBySurnameAndName(String surname, String name);
    Optional<Employee> findBySurnameAndNameAndPatronymic(String surname, String name, String patronymic);
    Employee save(Employee employee);
    void deleteById(Long id);
    void delete(Employee employee);
//    void deleteAll();
}
