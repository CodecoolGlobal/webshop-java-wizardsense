ALTER TABLE IF EXISTS ONLY products
    DROP CONSTRAINT IF EXISTS fk_product_category_id CASCADE;
ALTER TABLE IF EXISTS ONLY products
    DROP CONSTRAINT IF EXISTS fk_supplier_id CASCADE;
ALTER TABLE IF EXISTS ONLY carts
    DROP CONSTRAINT IF EXISTS fk_product_id CASCADE;
ALTER TABLE IF EXISTS ONLY carts
    DROP CONSTRAINT IF EXISTS fk_order_id CASCADE;
ALTER TABLE IF EXISTS ONLY orders
    DROP CONSTRAINT IF EXISTS fk_cart_id CASCADE;
ALTER TABLE IF EXISTS ONLY orders
    DROP CONSTRAINT IF EXISTS fk_user_id CASCADE;
ALTER TABLE IF EXISTS ONLY addresses
    DROP CONSTRAINT IF EXISTS fk_user_address_id CASCADE;


DROP TABLE IF EXISTS products;
DROP SEQUENCE IF EXISTS products_id_seq;
CREATE TABLE products
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR(15) NOT NULL,
    description         VARCHAR(40),
    default_price       INTEGER     NOT NULL,
    default_currency    CHAR(3),
    product_category_id INTEGER     NOT NULL,
    supplier_id         INTEGER     NOT NULL,
    image_src           VARCHAR(50)
);

DROP TABLE IF EXISTS suppliers;
DROP SEQUENCE IF EXISTS suppliers_id_seq;
CREATE TABLE suppliers
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(15) NOT NULL,
    description VARCHAR(40)
);

DROP TABLE IF EXISTS product_categories;
DROP SEQUENCE IF EXISTS product_categories_id_seq;
CREATE TABLE product_categories
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(15) NOT NULL,
    description VARCHAR(40),
    department  VARCHAR(10)
);

DROP TABLE IF EXISTS carts;
DROP SEQUENCE IF EXISTS carts_id_seq;
CREATE TABLE carts
(
    id               INTEGER NOT NULL ,
    product_id       INTEGER,
    product_quantity INTEGER
);

DROP TABLE IF EXISTS orders;
DROP SEQUENCE IF EXISTS orders_id_seq;
CREATE TABLE orders
(
    id      SERIAL PRIMARY KEY,
    cart_id INTEGER,
    user_id INTEGER,
    status  VARCHAR(10)
);

DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS users_id_seq;
CREATE TABLE users
(
    id        SERIAL PRIMARY KEY,
    user_name VARCHAR(15),
    password  VARCHAR(25)
);

DROP TABLE IF EXISTS addresses;
DROP SEQUENCE IF EXISTS addresses_id_seq;
CREATE TABLE addresses
(
    id           SERIAL PRIMARY KEY,
    user_id      INTEGER,
    name         VARCHAR(20),
    e_mail       VARCHAR(20),
    phone_number VARCHAR(15),
    country      VARCHAR(15),
    city         VARCHAR(10),
    zip_code     VARCHAR(8),
    address      VARCHAR(30)


);

ALTER TABLE ONLY products
    ADD CONSTRAINT fk_product_category_id FOREIGN KEY (product_category_id) REFERENCES product_categories (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_supplier_id FOREIGN KEY (supplier_id) REFERENCES suppliers (id) ON DELETE CASCADE;
ALTER TABLE ONLY carts
    ADD CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE;
ALTER TABLE ONLY orders
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE addresses
    ADD CONSTRAINT fk_user_address_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

