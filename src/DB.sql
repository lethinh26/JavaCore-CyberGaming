-- DROP DATABASE IF EXISTS cybergaming;
CREATE DATABASE cybergaming;
USE cybergaming;

CREATE TABLE roles (
	role_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
	user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(10) UNIQUE,
    balance DECIMAL(12,2) NOT NULL DEFAULT 0.00 CHECK(balance >= 0),
    role_id INT NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_users_role
		FOREIGN KEY (role_id) REFERENCES roles (role_id)
		ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE areas (
	area_id INT AUTO_INCREMENT PRIMARY KEY, 
    area_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE workstations (
	workstation_id INT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(100) NOT NULL, 
    area_id INT NOT NULL,
    specification TEXT NOT NULL,
    hourly_rate DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (hourly_rate >= 0),
    status ENUM('AVAILABLE', 'IN_USE', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    note VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
    CONSTRAINT fk_workstation_area
		FOREIGN KEY (area_id) REFERENCES areas (area_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE services (
	service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    category ENUM('FOOD', 'DRINK') NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    status ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK') NOT NULL DEFAULT 'ACTIVE',
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE bookings (
	booking_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    workstation_id INT NOT NULL,
    staff_id INT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NULL,
    total_amount DECIMAL(10,2) NULL, 
	booking_status ENUM('PENDING', 'ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_booking_time 
		CHECK (end_time IS NULL OR end_time > start_time),

	CONSTRAINT chk_booking_total_amount
		CHECK (total_amount IS NULL OR total_amount >= 0),
    
	CONSTRAINT fk_bookings_customer
        FOREIGN KEY (customer_id) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_bookings_workstation
        FOREIGN KEY (workstation_id) REFERENCES workstations(workstation_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_bookings_staff
        FOREIGN KEY (staff_id) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    customer_id INT NOT NULL,
    order_status ENUM('PENDING', 'CONFIRMED', 'SERVING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 CHECK (total_amount >= 0),
    staff_id INT NULL,
    note VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
        
	CONSTRAINT fk_orders_staff
		FOREIGN KEY (staff_id) REFERENCES users(user_id)
		ON UPDATE CASCADE
		ON DELETE SET NULL
);

CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    service_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    line_total DECIMAL(12,2) NOT NULL CHECK (line_total >= 0),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_order_items_service
        FOREIGN KEY (service_id) REFERENCES services(service_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE order_status_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    old_status ENUM('PENDING', 'CONFIRMED', 'SERVING', 'COMPLETED', 'CANCELLED') NOT NULL,
    new_status ENUM('PENDING', 'CONFIRMED', 'SERVING', 'COMPLETED', 'CANCELLED') NOT NULL,
    changed_by INT NOT NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note VARCHAR(255),

    CONSTRAINT fk_order_history_order
        FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_order_history_user
        FOREIGN KEY (changed_by) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);


CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_workstations_area_id ON workstations(area_id);
CREATE INDEX idx_workstations_status ON workstations(status);
CREATE INDEX idx_services_category ON services(category);
CREATE INDEX idx_services_status ON services(status);
CREATE INDEX idx_bookings_customer_id ON bookings(customer_id);
CREATE INDEX idx_bookings_workstation_id ON bookings(workstation_id);
CREATE INDEX idx_bookings_status ON bookings(booking_status);
CREATE INDEX idx_bookings_time_range ON bookings(start_time, end_time);
CREATE INDEX idx_orders_booking_id ON orders(booking_id);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(order_status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_service_id ON order_items(service_id);

INSERT INTO roles (role_name)
VALUES ('ADMIN'), ('STAFF'), ('CUSTOMER');

INSERT INTO areas (area_name, description)
VALUES
('STANDARD', 'Khu máy thường'),
('VIP', 'Khu máy VIP'),
('STREAM ROOM', 'Phòng stream');

INSERT INTO users (username, password_hash, email, full_name, phone, balance, role_id, status)
VALUES
('admin01', 'hashed_admin_password', 'admin@gmail.com', 'Nguyen Admin', '0900000001', 0, 1, 'ACTIVE'),
('staff01', 'hashed_staff_password', 'staff@gmail.com', 'Tran Staff', '0900000002', 0, 2, 'ACTIVE'),
('customer01', 'hashed_customer_password', 'customer@gmail.com', 'Le Customer', '0900000003', 500000, 3, 'ACTIVE');

INSERT INTO workstations (station_name, area_id, specification, hourly_rate, status, note)
VALUES
('May 01', 1, 'i5 / RTX 3060 / 16GB RAM', 10000, 'AVAILABLE', NULL),
('May 02', 1, 'i5 / RTX 3060 / 16GB RAM', 10000, 'AVAILABLE', NULL),
('VIP 01', 2, 'i7 / RTX 4070 / 32GB RAM', 20000, 'AVAILABLE', NULL),
('Stream 01', 3, 'i7 / RTX 4080 / 32GB RAM / Mic / Cam', 30000, 'MAINTENANCE', 'Dang bao tri');

INSERT INTO services (service_name, category, description, price, stock_quantity, status)
VALUES
    ('Mì tôm trứng', 'FOOD', 'Mì tôm kèm trứng', 25000, 50, 'ACTIVE'),
    ('Cơm chiên', 'FOOD', 'Cơm chiên dương châu', 35000, 30, 'ACTIVE'),
    ('Sting Dâu', 'DRINK', 'Nước tăng lực Sting dâu', 15000, 100, 'ACTIVE'),
    ('Pepsi', 'DRINK', 'Nước ngọt Pepsi', 12000, 80, 'ACTIVE');
