-- create table `employees`
CREATE TABLE employees (
    id_employee             BIGSERIAL   PRIMARY KEY,
    empl_surname            VARCHAR(50) NOT NULL,
    empl_name               VARCHAR(50) NOT NULL,
    empl_patronymic         VARCHAR(50),
    empl_role               VARCHAR(10) NOT NULL
        CHECK (empl_role IN ('manager', 'cashier')),
    salary                  DECIMAL(13, 4) NOT NULL,
    date_of_birth           DATE        NOT NULL,
    date_of_start           DATE        NOT NULL,
    empl_phone_number       VARCHAR(13) NOT NULL,
    empl_city               VARCHAR(50) NOT NULL,
    empl_street             VARCHAR(50) NOT NULL,
    empl_zip_code           VARCHAR(9)  NOT NULL
);


-- create table `categories`
CREATE TABLE categories (
    category_number           BIGSERIAL PRIMARY KEY,
    category_name             VARCHAR(50) NOT NULL
);


-- create table `products`
CREATE TABLE products (
    id_product          BIGSERIAL       PRIMARY KEY,
    category_number     BIGINT          NOT NULL,
    product_name        VARCHAR(50)     NOT NULL,
    manufacturer        VARCHAR(255)    NOT NULL,
    characteristics     VARCHAR(255)    NOT NULL,
    FOREIGN KEY (category_number) REFERENCES categories(category_number)
      ON UPDATE CASCADE ON DELETE NO ACTION
);


-- create table `customer_cards`
CREATE TABLE customer_cards (
    card_number         VARCHAR(13) PRIMARY KEY,
    cust_surname        VARCHAR(50) NOT NULL,
    cust_name           VARCHAR(50) NOT NULL,
    cust_patronymic     VARCHAR(50),
    cust_phone_number   VARCHAR(13) NOT NULL,
    cust_city           VARCHAR(50),
    cust_street         VARCHAR(50),
    cust_zip_code       VARCHAR(9),
    percent             INTEGER     NOT NULL
);


-- create table `store_products`
CREATE TABLE store_products (
    upc                 VARCHAR(12)     PRIMARY KEY,
    id_product          BIGINT          NOT NULL,
    upc_prom            VARCHAR(12),
    selling_price       DECIMAL(13, 4)  NOT NULL,
    products_number     INTEGER         NOT NULL,
    promotional_product BOOLEAN         NOT NULL,
    FOREIGN KEY (id_product)    REFERENCES products(id_product)
        ON UPDATE CASCADE ON DELETE NO ACTION,
    FOREIGN KEY (upc_prom)      REFERENCES store_products(upc)
        ON UPDATE CASCADE ON DELETE SET NULL
);


-- create table 'checks'
CREATE TABLE checks (
    check_number        VARCHAR(10) PRIMARY KEY,
    id_employee         BIGINT NOT NULL,
    card_number         VARCHAR(13),
    print_date          TIMESTAMP NOT NULL,
    sum_total           DECIMAL(13, 4),
    vat                 DECIMAL(13, 4),
    FOREIGN KEY (id_employee) REFERENCES employees(id_employee) ON UPDATE CASCADE ON DELETE NO ACTION,
    FOREIGN KEY (card_number) REFERENCES customer_cards(card_number) ON UPDATE CASCADE ON DELETE NO ACTION
);


-- create table `sales`
CREATE TABLE sales (
    upc                 VARCHAR(12) NOT NULL,
    check_number        VARCHAR(10) NOT NULL,
    product_number      INTEGER NOT NULL,
    selling_price       DECIMAL(13, 4) NOT NULL,
    PRIMARY KEY (upc, check_number),
    FOREIGN KEY (upc) REFERENCES store_products(upc) ON UPDATE CASCADE ON DELETE NO ACTION,
    FOREIGN KEY (check_number) REFERENCES checks(check_number) ON UPDATE CASCADE ON DELETE CASCADE
);
