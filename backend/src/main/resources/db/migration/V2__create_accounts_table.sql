CREATE TABLE accounts (
    id_account  BIGSERIAL PRIMARY KEY,
    id_employee BIGINT       NOT NULL UNIQUE,
    login       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    authority   VARCHAR(8)   NOT NULL
        CHECK (authority IN ('admin', 'user')),

    FOREIGN KEY (id_employee) REFERENCES employees (id_employee)
        ON UPDATE CASCADE ON DELETE CASCADE
);