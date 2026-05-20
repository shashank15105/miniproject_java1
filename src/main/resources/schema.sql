CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS workspaces (
    workspace_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    available_seats INT NOT NULL,
    price_per_hour INT NOT NULL,
    CHECK (capacity >= 0),
    CHECK (available_seats >= 0),
    CHECK (available_seats <= capacity),
    CHECK (price_per_hour >= 0)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    workspace_id VARCHAR(50) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    total_price INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (workspace_id) REFERENCES workspaces(workspace_id),
    CHECK (end_time > start_time),
    CHECK (total_price >= 0)
);

CREATE TABLE IF NOT EXISTS workspace_amenities (
    amenity_id VARCHAR(50) PRIMARY KEY,
    workspace_id VARCHAR(50) NOT NULL,
    amenity_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (workspace_id) REFERENCES workspaces(workspace_id)
);

CREATE TABLE IF NOT EXISTS workspace_reviews (
    review_id VARCHAR(50) PRIMARY KEY,
    workspace_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    rating INT NOT NULL,
    review_text VARCHAR(255) NOT NULL,
    reviewed_on DATETIME NOT NULL,
    FOREIGN KEY (workspace_id) REFERENCES workspaces(workspace_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    CHECK (rating >= 1),
    CHECK (rating <= 5)
);
