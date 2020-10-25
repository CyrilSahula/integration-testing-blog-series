CREATE SEQUENCE IF NOT EXISTS customer_seq_pk;

CREATE TABLE IF NOT EXISTS customer (
    id BIGSERIAL,
    identification_number VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL
);