INSERT INTO users (user_id, name, email, phone)
SELECT 'U1', 'Aarav Sharma', 'aarav@example.com', '9876543210'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id = 'U1');

INSERT INTO users (user_id, name, email, phone)
SELECT 'U2', 'Diya Patel', 'diya@example.com', '9876543211'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id = 'U2');

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W1', 'WeWork BKC', 'Mumbai', 20, 20, 200)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W2', 'BHIVE HSR Layout', 'Bengaluru', 8, 8, 500)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W3', 'Bootstart Baner', 'Pune', 16, 16, 180)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W4', '91Springboard HITEC City', 'Hyderabad', 24, 24, 220)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W5', 'Workafella OMR', 'Chennai', 10, 10, 450)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W6', 'Innov8 Connaught Place', 'Delhi', 18, 18, 210)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W7', 'WeWork Two Horizon Center', 'Gurugram', 6, 6, 650)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W8', 'Awfis Salt Lake Sector V', 'Kolkata', 14, 14, 170)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W9', 'Smartworks Logix Cyber Park', 'Noida', 12, 12, 550)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W10', 'Clay Coworking Anjuna', 'Goa', 9, 9, 300)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W11', 'IndiQube Arena BKC', 'Mumbai', 22, 22, 240)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W12', 'Awfis Andheri East', 'Mumbai', 18, 18, 190)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W13', 'WeWork Koramangala', 'Bengaluru', 26, 26, 260)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W14', 'IndiQube Alpha Bellandur', 'Bengaluru', 20, 20, 210)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W15', 'UrbanWrk Kharadi', 'Pune', 18, 18, 195)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W16', 'Awfis Viman Nagar', 'Pune', 12, 12, 175)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W17', 'iKeva Madhapur', 'Hyderabad', 16, 16, 205)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W18', 'Regus Banjara Hills', 'Hyderabad', 10, 10, 230)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W19', 'The Executive Zone Guindy', 'Chennai', 14, 14, 260)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W20', 'Innov8 T Nagar', 'Chennai', 11, 11, 240)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W21', 'Smartworks Nehru Place', 'Delhi', 20, 20, 225)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W22', 'Awfis Aerocity', 'Delhi', 15, 15, 250)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W23', 'Spring House Udyog Vihar', 'Gurugram', 13, 13, 280)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W24', 'Innov8 Cyber City', 'Gurugram', 9, 9, 320)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W25', 'Workzone Park Street', 'Kolkata', 12, 12, 185)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W26', 'MyCube Elgin Road', 'Kolkata', 10, 10, 165)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W27', 'Awfis Sector 62', 'Noida', 17, 17, 215)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W28', 'Incuspaze Noida Expressway', 'Noida', 14, 14, 235)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W29', 'NomadGao Assagao', 'Goa', 8, 8, 340)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour)
VALUES ('W30', '91Springboard Panaji', 'Goa', 10, 10, 275)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
location = VALUES(location),
capacity = VALUES(capacity),
price_per_hour = VALUES(price_per_hour),
available_seats = LEAST(VALUES(capacity), GREATEST(available_seats, 0));

INSERT INTO workspace_amenities (amenity_id, workspace_id, amenity_name)
SELECT 'A1', 'W1', 'High-Speed WiFi'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_amenities WHERE amenity_id = 'A1');

INSERT INTO workspace_amenities (amenity_id, workspace_id, amenity_name)
SELECT 'A2', 'W1', 'Meeting Booths'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_amenities WHERE amenity_id = 'A2');

INSERT INTO workspace_amenities (amenity_id, workspace_id, amenity_name)
SELECT 'A3', 'W2', 'Conference Screen'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_amenities WHERE amenity_id = 'A3');

INSERT INTO workspace_amenities (amenity_id, workspace_id, amenity_name)
SELECT 'A4', 'W6', 'Coffee Bar'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_amenities WHERE amenity_id = 'A4');

INSERT INTO workspace_amenities (amenity_id, workspace_id, amenity_name)
SELECT 'A5', 'W11', '24x7 Access'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_amenities WHERE amenity_id = 'A5');

INSERT INTO workspace_amenities (amenity_id, workspace_id, amenity_name)
SELECT 'A6', 'W13', 'Phone Booth'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_amenities WHERE amenity_id = 'A6');

INSERT INTO workspace_reviews (review_id, workspace_id, user_id, rating, review_text, reviewed_on)
SELECT 'R1', 'W1', 'U1', 5, 'Great location, smooth check-in, and reliable internet.', '2026-05-01 10:00:00'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_reviews WHERE review_id = 'R1');

INSERT INTO workspace_reviews (review_id, workspace_id, user_id, rating, review_text, reviewed_on)
SELECT 'R2', 'W3', 'U2', 4, 'Quiet workspace with good seating and easy parking.', '2026-05-03 14:30:00'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_reviews WHERE review_id = 'R2');

INSERT INTO workspace_reviews (review_id, workspace_id, user_id, rating, review_text, reviewed_on)
SELECT 'R3', 'W13', 'U1', 5, 'Perfect for team sessions and the staff was helpful.', '2026-05-05 11:15:00'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_reviews WHERE review_id = 'R3');

INSERT INTO workspace_reviews (review_id, workspace_id, user_id, rating, review_text, reviewed_on)
SELECT 'R4', 'W21', 'U2', 4, 'Nice ambience and good connectivity for meetings.', '2026-05-06 16:45:00'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM workspace_reviews WHERE review_id = 'R4');
