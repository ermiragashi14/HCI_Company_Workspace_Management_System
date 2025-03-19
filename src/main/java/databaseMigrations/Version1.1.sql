
-- NEW ALTERS

ALTER TABLE user ADD CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE;

ALTER TABLE workspace ADD CONSTRAINT fk_workspace_company FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE;

ALTER TABLE session ADD CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

ALTER TABLE reservation ADD CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

ALTER TABLE reservation ADD CONSTRAINT fk_reservation_workspace FOREIGN KEY (workspace_id) REFERENCES workspace(id) ON DELETE CASCADE;

ALTER TABLE notification DROP FOREIGN KEY notification_ibfk_1;
ALTER TABLE notification DROP FOREIGN KEY notification_ibfk_2;

ALTER TABLE notification
ADD CONSTRAINT fk_notification_user FOREIGN KEY (user_id)
REFERENCES user(id) ON DELETE CASCADE;

ALTER TABLE notification
ADD CONSTRAINT fk_notification_reservation FOREIGN KEY (reservation_id)
REFERENCES reservation(id) ON DELETE CASCADE;

-- TESTING THE INSERTATION, DELETE ON CASCADE CONSTRAINTS...

INSERT INTO company (name, email, phone_number)
VALUES ('TechCorp', 'contact@techcorp.com', '+38344123456');

SELECT * FROM company;

INSERT INTO user (company_id, role, password_hash, full_name, email, phone_number, status)
VALUES
(1, 'SUPER_ADMIN', 'hashed_pass', 'Admin One', 'admin1@techcorp.com', '+38344123457', 'ACTIVE'),
(1, 'ADMIN', 'hashed_pass', 'Admin Two', 'admin2@techcorp.com', '+38344123458', 'ACTIVE'),
(1, 'STAFF', 'hashed_pass', 'John Doe', 'johndoe@techcorp.com', '+38344123459', 'ACTIVE');

SELECT * FROM user;

INSERT INTO workspace (company_id, name, capacity, description)
VALUES
(1, 'Conference Room A', 10, 'A large meeting room'),
(1, 'Open Space Desk', 5, 'Shared workspace');

SELECT * FROM workspace;

INSERT INTO reservation (user_id, workspace_id, date, start_time, end_time, status)
VALUES
(1, 2, '2025-03-15', '10:00:00', '12:00:00', 'CONFIRMED');

SELECT * FROM reservation;

INSERT INTO session (user_id, last_active, expires_at, is_active)
VALUES
(1, NOW(), NOW() + INTERVAL 30 MINUTE, TRUE);

SELECT * FROM session;

INSERT INTO notification (user_id, reservation_id, message, read_status)
VALUES
(1, 1, 'Your reservation has been confirmed.', FALSE);

SELECT * FROM notification;
SELECT * FROM user;
SELECT * FROM reservation;

DELETE FROM user WHERE id = 1;

INSERT INTO reservation (user_id, workspace_id, date, start_time, end_time, status)
VALUES
(2, 2, '2025-03-15', '10:00:00', '12:00:00', 'CONFIRMED');

INSERT INTO notification (user_id, reservation_id, message, read_status)
VALUES
(2, 2, 'Your reservation has been confirmed.', FALSE);

INSERT INTO session (user_id, last_active, expires_at, is_active)
VALUES
(2, NOW(), NOW() + INTERVAL 30 MINUTE, TRUE);

select * from workspace;

DELETE FROM workspace WHERE id = 2;

DELETE FROM company WHERE id = 1;
-------------------------------------
ALTER TABLE reservation MODIFY status ENUM('CONFIRMED', 'CANCELED', 'COMPLETED') DEFAULT 'CONFIRMED';

