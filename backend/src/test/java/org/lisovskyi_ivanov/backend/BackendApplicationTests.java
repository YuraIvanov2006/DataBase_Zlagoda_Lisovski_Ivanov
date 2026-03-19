package org.lisovskyi_ivanov.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Nested
    @DisplayName("Employee Tests")
    class EmployeeTests {
        @Test
        void testEmployeeName() {
            String name = "name_test";
            Employee e = Employee.builder().emplName(name).build();
            assertEquals(name, e.getEmplName());
        }

        @Test
        void testEmployeeSurname() {
            String surname = "surname_test";
            Employee e = Employee.builder().emplSurname(surname).build();
            assertEquals(surname, e.getEmplSurname());
        }
    }
}
