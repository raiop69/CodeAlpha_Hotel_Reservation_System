CREATE DATABASE HotelReservationDB;
GO
USE HotelReservationDB;
GO
CREATE TABLE Users (
    user_id     INT IDENTITY(1,1) PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,   -- store SHA-256 hash
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    phone       VARCHAR(20),
    role        VARCHAR(10)  NOT NULL DEFAULT 'user'  -- 'admin' or 'user'
                CHECK (role IN ('admin','user')),
    created_at  DATETIME     DEFAULT GETDATE()
);
CREATE TABLE RoomCategories (
    category_id   INT IDENTITY(1,1) PRIMARY KEY,
    category_name VARCHAR(50)  NOT NULL UNIQUE,  -- Standard, Deluxe, Suite
    description   VARCHAR(255),
    base_price    DECIMAL(10,2) NOT NULL
);
CREATE TABLE Rooms (
    room_id       INT IDENTITY(1,1) PRIMARY KEY,
    room_number   VARCHAR(10)  NOT NULL UNIQUE,
    category_id   INT          NOT NULL REFERENCES RoomCategories(category_id),
    floor         INT          NOT NULL DEFAULT 1,
    capacity      INT          NOT NULL DEFAULT 2,
    amenities     VARCHAR(500),
    status        VARCHAR(20)  NOT NULL DEFAULT 'available'
                  CHECK (status IN ('available','occupied','maintenance')),
    price_per_night DECIMAL(10,2) NOT NULL
);
CREATE TABLE Reservations (
    reservation_id  INT IDENTITY(1,1) PRIMARY KEY,
    user_id         INT          NOT NULL REFERENCES Users(user_id),
    room_id         INT          NOT NULL REFERENCES Rooms(room_id),
    check_in        DATE         NOT NULL,
    check_out       DATE         NOT NULL,
    total_price     DECIMAL(10,2) NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'confirmed'
                    CHECK (status IN ('confirmed','cancelled','completed')),
    created_at      DATETIME     DEFAULT GETDATE(),
    CONSTRAINT chk_dates CHECK (check_out > check_in)
);
CREATE TABLE Payments (
    payment_id      INT IDENTITY(1,1) PRIMARY KEY,
    reservation_id  INT          NOT NULL REFERENCES Reservations(reservation_id),
    amount          DECIMAL(10,2) NOT NULL,
    payment_method  VARCHAR(30)  NOT NULL DEFAULT 'Cash'
                    CHECK (payment_method IN ('Cash','Credit Card','Debit Card','Online')),
    payment_status  VARCHAR(20)  NOT NULL DEFAULT 'paid'
                    CHECK (payment_status IN ('paid','refunded','pending')),
    payment_date    DATETIME     DEFAULT GETDATE()
);
INSERT INTO Users (username, password, full_name, email, phone, role)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        'System Admin', 'admin@hotel.com', '0300-0000000', 'admin');
INSERT INTO Users (username, password, full_name, email, phone, role)
VALUES ('user1', '0a041b9462caa4a31bac3567e0b6e6fd9100787db2ab433d96f6d178cabfce90',
        'Ali Khan', 'ali@email.com', '0311-1234567', 'user');

INSERT INTO RoomCategories (category_name, description, base_price) VALUES
('Standard', 'Comfortable room with basic amenities', 5000.00),
('Deluxe',   'Spacious room with premium furnishings and city view', 9000.00),
('Suite',    'Luxury suite with separate living area and premium services', 18000.00);
INSERT INTO Rooms (room_number, category_id, floor, capacity, amenities, status, price_per_night) VALUES
('101', 1, 1, 2, 'WiFi, TV, AC, Attached Bath', 'available', 5000.00),
('102', 1, 1, 2, 'WiFi, TV, AC, Attached Bath', 'available', 5000.00),
('201', 2, 2, 3, 'WiFi, 55" TV, AC, Mini Bar, City View', 'available', 9000.00),
('202', 2, 2, 3, 'WiFi, 55" TV, AC, Mini Bar, City View', 'available', 9000.00),
('301', 3, 3, 4, 'WiFi, 65" TV, AC, Jacuzzi, Living Room, Balcony', 'available', 18000.00),
('302', 3, 3, 4, 'WiFi, 65" TV, AC, Jacuzzi, Living Room, Balcony', 'available', 18000.00);
GO
CREATE PROCEDURE GetAvailableRooms
    @CheckIn  DATE,
    @CheckOut DATE,
    @Category VARCHAR(50) = NULL
AS
BEGIN
    SELECT r.room_id, r.room_number, rc.category_name, r.floor,
           r.capacity, r.amenities, r.price_per_night
    FROM   Rooms r
    JOIN   RoomCategories rc ON r.category_id = rc.category_id
    WHERE  r.status = 'available'
      AND  (@Category IS NULL OR rc.category_name = @Category)
      AND  r.room_id NOT IN (
               SELECT res.room_id FROM Reservations res
               WHERE  res.status = 'confirmed'
                 AND  NOT (res.check_out <= @CheckIn OR res.check_in >= @CheckOut)
           );
END;
GOs
PRINT 'Database setup complete! Tables created and seeded successfully.';
