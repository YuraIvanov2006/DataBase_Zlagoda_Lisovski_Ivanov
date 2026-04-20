-- 1. Додаємо тестового менеджера як співробітника
INSERT INTO employees (
  empl_surname, empl_name, empl_patronymic, empl_role, salary,
  date_of_birth, date_of_start, empl_phone_number, empl_city, empl_street, empl_zip_code
) VALUES (
  'Admin', 'Manager', NULL, 'manager', 20000,
  '1990-01-01', CURRENT_DATE, '+380960702344', 'Lviv', 'Main st', '79000'
);

-- 2. Створюємо акаунт для нього (bcrypt хеш пароля 'password')
INSERT INTO accounts (id_employee, login, password)
SELECT id_employee, 'manager', '$2a$10$Xl0yhvzLIaJCDdKBS0Lld.ksK7c2Wss1jfpOwqrE1oIKvDDn7Nw2S'
FROM employees
WHERE empl_surname = 'Admin' AND empl_name = 'Manager'
LIMIT 1;