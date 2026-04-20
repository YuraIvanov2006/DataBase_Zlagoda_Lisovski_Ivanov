-- V5__reset_accounts.sql
-- Видаляємо всі існуючі акаунти
DELETE FROM accounts;

-- Видаляємо всіх тестових співробітників (якщо потрібно чистий старт)
DELETE FROM employees WHERE empl_surname = 'Admin' AND empl_name = 'Manager';

-- Додаємо свіжого тестового менеджера
INSERT INTO employees (
    empl_surname, empl_name, empl_patronymic, empl_role, salary,
    date_of_birth, date_of_start, empl_phone_number,
    empl_city, empl_street, empl_zip_code
) VALUES (
             'Admin', 'Manager', NULL, 'manager', 20000,
             '1990-01-01', CURRENT_DATE, '+380000000000',
             'Lviv', 'Main st', '79000'
         );

-- Створюємо акаунт для нього
-- Логін: manager | Пароль: password
INSERT INTO accounts (id_employee, login, password)
SELECT id_employee,
       'manager',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
FROM employees
WHERE empl_surname = 'Admin'
  AND empl_name = 'Manager'
ORDER BY id_employee DESC
LIMIT 1;