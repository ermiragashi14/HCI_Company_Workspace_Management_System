use workspacemanagement;

-- Company Table
CREATE TABLE company (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Table (Super Admin, Regular Admin, Staff)
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    role ENUM('SUPER_ADMIN', 'ADMIN', 'STAFF') NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE',
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id),
    UNIQUE (company_id, email),
     UNIQUE (company_id, role)
);

-- Session Table (Tracks Active Users)
CREATE TABLE session (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP DEFAULT (NOW() + INTERVAL 30 MINUTE),
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Workspace Table (Tracks Office Spaces)
CREATE TABLE workspace (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Reservation Table (Manages Workspace Reservations)
CREATE TABLE reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    workspace_id INT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('CONFIRMED', 'CANCELED', 'EXPIRED') DEFAULT 'CONFIRMED',
    previous_status ENUM('CONFIRMED', 'CANCELED', 'EXPIRED') NULL,
    modified_by INT NULL,
    modified_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (workspace_id) REFERENCES workspace(id),
    FOREIGN KEY (modified_by) REFERENCES user(id)
);

-- Notification Table (Reservation Alerts & Messages)
CREATE TABLE notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    reservation_id INT NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (reservation_id) REFERENCES reservation(id)
);

-- Audit Log Table (Tracks Admin, Super Admin, & Staff Actions)
CREATE TABLE audit_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    company_id INT NOT NULL,
    action_type VARCHAR(255) NOT NULL,
    details TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Users will be automatically logged out if inactive for 30 minutes
CREATE EVENT auto_logout_expired_sessions
ON SCHEDULE EVERY 10 MINUTE
DO
    UPDATE session SET is_active = FALSE WHERE last_active < NOW() - INTERVAL 30 MINUTE;


-- Cleanup Old Audit Logs (Automatically Delete Older Than 6 Months)
CREATE EVENT cleanup_old_logs
ON SCHEDULE EVERY 1 DAY
DO
    DELETE FROM audit_log WHERE timestamp < NOW() - INTERVAL 6 MONTH;


ALTER TABLE notification ADD COLUMN read_status BOOLEAN DEFAULT FALSE;


