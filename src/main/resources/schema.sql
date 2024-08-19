CREATE TABLE operational_expense_items (
    id SERIAL PRIMARY KEY ,
    item VARCHAR(100) NOT NULL,
    remarks VARCHAR(200)
);

CREATE TABLE units (
    id SERIAL PRIMARY KEY ,
    name VARCHAR(20) NOT NULL,
    symbol VARCHAR(10) NOT NULL
);

CREATE TABLE materials (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    unit_id INTEGER REFERENCES units(id),
);

CREATE TABLE material_stock (
    material_id INTEGER PRIMARY KEY REFERENCES materials(id),
    quantity DOUBLE PRECISION NOT NULL,
    min_quantity DOUBLE PRECISION NOT NULL,
    last_purchase_date TIMESTAMP NOT NULL
);

CREATE TABLE vendors (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    status VARCHAR(10) NOT NULL
);

CREATE TABLE flocks (
    id UUID PRIMARY KEY,
    count INTEGER NOT NULL,
    remarks VARCHAR NOT NULL,
    date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE production (
    id UUID PRIMARY KEY,
    produced_count INTEGER NOT NULL,
    production_percentage DOUBLE PRECISION NOT NULL,
    broken_count INTEGER NOT NULL,
    broken_reason VARCHAR(100) NOT NULL,
    self_use_count INTEGER NOT NULL,
    gift_count INTEGER NOT NULL,
    saleable_count INTEGER NOT NULL,
    production_date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE sale (
    id UUID PRIMARY KEY,
    sold_count INTEGER NOT NULL,
    rate DOUBLE PRECISION NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    paid BOOLEAN NOT NULL,
    vendor_id INTEGER REFERENCES vendors(id),
    sale_date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE material_purchase (
    id UUID PRIMARY KEY,
    material_id INTEGER REFERENCES materials(id),
    rate DOUBLE PRECISION NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE material_consumption (
    id UUID PRIMARY KEY,
    material_id INTEGER REFERENCES materials(id),
    quantity DOUBLE PRECISION NOT NULL,
    consumption_date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE operational_expense (
    id UUID PRIMARY KEY,
    item_id INTEGER REFERENCES operational_expense_items(id),
    amount DOUBLE PRECISION NOT NULL,
    expense_date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);


