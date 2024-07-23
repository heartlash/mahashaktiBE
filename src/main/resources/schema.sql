CREATE TABLE operational_expense_items (
    id INTEGER PRIMARY KEY,
    item VARCHAR(100) NOT NULL,
    remarks VARCHAR(200)
);

CREATE TABLE units (
    id INTEGER PRIMARY KEY,
    name VARCHAR NOT NULL,
    symbol VARCHAR NOT NULL
);

CREATE TABLE materials (
    id INTEGER PRIMARY KEY,
    name VARCHAR NOT NULL,
    unit_id INTEGER REFERENCES units(id)
);

CREATE TABLE vendors (
    id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    status BOOLEAN NOT NULL
);

CREATE TABLE flocks (
    id UUID PRIMARY KEY,
    count INTEGER NOT NULL,
    added BOOLEAN NOT NULL,
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
    quantity INTEGER NOT NULL,
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
    unit_id INTEGER REFERENCES units(id),
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
    unit_id INTEGER REFERENCES units(id),
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


