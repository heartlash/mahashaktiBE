CREATE TABLE operational_expense_items (
    id SERIAL PRIMARY KEY ,
    item VARCHAR(100) NOT NULL
);

CREATE TABLE units (
    id SERIAL PRIMARY KEY ,
    name VARCHAR(20) NOT NULL,
    symbol VARCHAR(10) NOT NULL
);

CREATE TABLE materials (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    unit_id INTEGER REFERENCES units(id)
);

CREATE TABLE egg_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE material_stock (
    material_id INTEGER PRIMARY KEY REFERENCES materials(id),
    quantity DOUBLE PRECISION NOT NULL,
    min_quantity DOUBLE PRECISION NOT NULL,
    last_restock_date TIMESTAMP NOT NULL,
    last_restock_quantity DOUBLE PRECISION NOT NULL
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
    shed_id INTEGER REFERENCES sheds(id) NOT NULL,
    count INTEGER NOT NULL,
    remarks VARCHAR NOT NULL,
    date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE egg_stock (
    id UUID PRIMARY KEY,
    egg_type_id INTEGER REFERENCES egg_types(id) NOT NULL,
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
    shed_id INTEGER REFERENCES sheds(id) NOT NULL,
    broken_count INTEGER NOT NULL,
    broken_reason VARCHAR(100) NOT NULL,
    waste_count INTEGER NOT NULL,
    self_use_count INTEGER NOT NULL,
    gift_count INTEGER NOT NULL,
    production_date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE sale (
    id UUID PRIMARY KEY,
    egg_type_id INTEGER REFERENCES egg_types(id),
    sold_count INTEGER NOT NULL,
    rate DOUBLE PRECISION NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    paid_amount DOUBLE PRECISION NOT NULL,
    payment_remarks VARCHAR(50),
    paid BOOLEAN NOT NULL,
    vendor_id INTEGER REFERENCES vendors(id),
    sale_date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
 
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    sale_id UUID REFERENCES sale(id),
    amount DOUBLE PRECISION NOT NULL,
    vendor_id INTEGER REFERENCES vendors(id),
    remarks VARCHAR NOT NULL,
    payment_date TIMESTAMP NOT NULL,
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
    shed_id INTEGER REFERENCES sheds(id),
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
    remarks VARCHAR(200),
    expense_date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE material_restock (
    id UUID PRIMARY KEY,
    material_id INTEGER REFERENCES materials(id),
    quantity DOUBLE PRECISION NOT NULL,
    restock_date TIMESTAMP NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE sheds (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50),
    active BOOLEAN NOT NULL,
    baby BOOLEAN NOT NULL,
    genesis_date TIMESTAMP
);

CREATE TABLE feed_composition (
    id UUID PRIMARY KEY,
    shed_id INTEGER REFERENCES sheds(id),
    material_id INTEGER REFERENCES materials(id),
    quantity_per_tonne DOUBLE PRECISION NOT NULL,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP
);

CREATE TABLE feed_quantity (
    id SERIAL PRIMARY KEY,
    shed_id INTEGER REFERENCES sheds(id),
    quantity_per_bird DOUBLE PRECISION NOT NULL,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP
);
