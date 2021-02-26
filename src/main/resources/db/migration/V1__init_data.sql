CREATE SEQUENCE IF NOT EXISTS customer_seq_pk;
CREATE TABLE IF NOT EXISTS customer (
    id BIGSERIAL,
    identification_number VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS order_seq_pk;
CREATE TABLE IF NOT EXISTS `order` (
    id BIGSERIAL,
    `number` VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL
);