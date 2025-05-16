CREATE DATABASE WorkspaceManagement;
USE WorkspaceManagement;

-- Company Table
CREATE TABLE company (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Table (Super Admin, Admin, Staff)
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    role ENUM('SUPER_ADMIN', 'ADMIN', 'STAFF') NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE',
    otp_code VARCHAR(10),
    otp_expiry DATETIME,
    avatar_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (company_id, email),
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

-- Workspace Table (Tracks Office Spaces)
CREATE TABLE workspace (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

-- Reservation Table (Manages Workspace Reservations)
CREATE TABLE reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    workspace_id INT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('CONFIRMED', 'CANCELED', 'COMPLETED') DEFAULT 'CONFIRMED',
    previous_status ENUM('CONFIRMED', 'CANCELED', 'COMPLETED') NULL,
    modified_by INT NULL,
    modified_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (workspace_id) REFERENCES workspace(id) ON DELETE CASCADE,
    FOREIGN KEY (modified_by) REFERENCES user(id)
);

-- Notification Table (Reservation Alerts & Messages)

CREATE TABLE notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receiver_id INT NOT NULL,
    sender_id INT NULL,
    message TEXT NOT NULL,
    type ENUM('INFO', 'REMINDER', 'ACTION', 'WARNING') DEFAULT 'INFO',
    read_status BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (receiver_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES user(id)
);

-- Audit Log Table (Tracks Actions)
CREATE TABLE audit_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    company_id INT NOT NULL,
    action_type VARCHAR(255) NOT NULL,
    details TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

-- Scheduled Event: Auto Logout inactive users after 30 mins
--CREATE EVENT auto_logout_expired_sessions
--ON SCHEDULE EVERY 10 MINUTE
--DO
--   UPDATE session SET is_active = FALSE WHERE last_active < NOW() - INTERVAL 30 MINUTE;

-- Scheduled Event: Cleanup audit logs older than 6 months
DROP EVENT IF EXISTS cleanup_old_logs;
CREATE EVENT cleanup_old_logs
ON SCHEDULE EVERY 1 DAY
DO
    DELETE FROM audit_log WHERE timestamp < NOW() - INTERVAL 6 MONTH;


