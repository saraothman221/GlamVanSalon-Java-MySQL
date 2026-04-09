-- ------------------------------------------------------
-- RESET DATABASE
-- ------------------------------------------------------
DROP DATABASE IF EXISTS SalonManagement;

CREATE DATABASE SalonManagement;
USE SalonManagement;

-- ------------------------------------------------------
-- USER TABLE (MAIN ACCOUNT TABLE)
-- ------------------------------------------------------
CREATE TABLE User (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Manager', 'Customer', 'Employee') NOT NULL DEFAULT 'Customer'
);

ALTER TABLE User AUTO_INCREMENT = 1000;

-- ------------------------------------------------------
-- MANAGER TABLE
-- ------------------------------------------------------
CREATE TABLE Manager (
    managerID INT PRIMARY KEY,
    FOREIGN KEY (managerID)
        REFERENCES User (userID)
        ON DELETE CASCADE
);

-- ------------------------------------------------------
-- CUSTOMER TABLE
-- ------------------------------------------------------
CREATE TABLE Customer (
    customerID INT PRIMARY KEY,
    phone  VARCHAR(20),
    address VARCHAR(255) NULL,
    FOREIGN KEY (customerID)
        REFERENCES User (userID)
        ON DELETE CASCADE
);

-- ------------------------------------------------------
-- EMPLOYEE TABLE
-- ------------------------------------------------------
CREATE TABLE Employee (
    employeeID INT PRIMARY KEY,
    specialization VARCHAR(100),
    salary DECIMAL(10,2),
    phone VARCHAR(20),   -- Added phone here
    FOREIGN KEY (employeeID)
        REFERENCES User (userID)
        ON DELETE CASCADE
);

-- ------------------------------------------------------
-- SERVICE TABLE
-- ------------------------------------------------------
CREATE TABLE Service (
    serviceID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- ------------------------------------------------------
-- APPOINTMENT TABLE
-- ------------------------------------------------------
CREATE TABLE Appointment (
    appointmentID INT AUTO_INCREMENT PRIMARY KEY,
    customerID INT,
    serviceID INT,
    date DATE NOT NULL,
    time TIME NOT NULL,
    status ENUM('Scheduled', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
    
    FOREIGN KEY (customerID)
        REFERENCES Customer (customerID)
        ON DELETE CASCADE,

    FOREIGN KEY (serviceID)
        REFERENCES Service (serviceID)
        ON DELETE CASCADE
);

-- ------------------------------------------------------
-- PAYMENT TABLE
-- ------------------------------------------------------
CREATE TABLE Payment (
    paymentID INT AUTO_INCREMENT PRIMARY KEY,
    appointmentID INT UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    method ENUM('Cash', 'CreditCard') NOT NULL,

    FOREIGN KEY (appointmentID)
        REFERENCES Appointment (appointmentID)
        ON DELETE CASCADE
);

-- ------------------------------------------------------
-- CASH TABLE
-- ------------------------------------------------------
CREATE TABLE Cash (
    cashID INT PRIMARY KEY,
    receivedBy VARCHAR(100),
    FOREIGN KEY (cashID)
        REFERENCES Payment (paymentID)
        ON DELETE CASCADE
);

-- ------------------------------------------------------
-- CREDIT CARD TABLE
-- ------------------------------------------------------
CREATE TABLE CreditCard (
    creditID INT PRIMARY KEY,
    cvv_num VARCHAR(20) NOT NULL,
    cardNumber VARCHAR(20) NOT NULL,
    expiryDate VARCHAR(20) NOT NULL,

    FOREIGN KEY (creditID)
        REFERENCES Payment (paymentID)
        ON DELETE CASCADE
);

-- ------------------------------------------------------
-- FEEDBACK TABLE
-- ------------------------------------------------------
CREATE TABLE Feedback (
    feedbackID INT AUTO_INCREMENT PRIMARY KEY,
    customerID INT,
    serviceID INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,

    FOREIGN KEY (customerID)
        REFERENCES Customer (customerID)
        ON DELETE CASCADE,

    FOREIGN KEY (serviceID)
        REFERENCES Service (serviceID)
        ON DELETE CASCADE
);

-- ------------------------------------------------------
-- INSERT USERS
-- ------------------------------------------------------
INSERT INTO User (name, username, email, password, role) VALUES
('Layla Manager', 'laylam', 'layla@salon.com', 'pass@123', 'Manager'),
('Sara Customer', 'sarac', 'sara@client.com', 'cust@123', 'Customer'),
('Amal Customer', 'amalc', 'amal@client.com', 'cust@456', 'Customer'),
('Nora Employee', 'norae', 'nora@salon.com', 'emp@123', 'Employee'),
('Rania Employee', 'rania', 'rania@salon.com', 'emp@456', 'Employee');

-- ------------------------------------------------------
-- INSERT MANAGER
-- ------------------------------------------------------
INSERT INTO Manager (managerID) VALUES (1000);

-- ------------------------------------------------------
-- INSERT CUSTOMERS
-- ------------------------------------------------------
INSERT INTO Customer (customerID, phone, address) VALUES
(1001, '0551234567', 'Riyadh'),
(1002, '0569876543', 'Jeddah');

-- ------------------------------------------------------
-- INSERT EMPLOYEES (phone added)
-- ------------------------------------------------------
INSERT INTO Employee (employeeID, specialization, salary, phone) VALUES
(1003, 'Hair Stylist', 5000.00, '0553000001'),
(1004, 'Makeup Artist', 5500.00, '0553000002');

-- ------------------------------------------------------
-- INSERT SERVICES
-- ------------------------------------------------------
INSERT INTO Service (name, price) VALUES
('Haircut', 50.00),
('Hair Color', 120.00),
('Facial', 80.00),
('Manicure', 40.00),
('Makeup', 150.00);

-- ------------------------------------------------------
-- INSERT APPOINTMENTS
-- ------------------------------------------------------
INSERT INTO Appointment (customerID, serviceID, date, time, status) VALUES
(1001, 1, '2025-05-01', '10:00:00', 'Scheduled'),
(1001, 5, '2025-05-03', '12:00:00', 'Completed'),
(1002, 3, '2025-05-02', '09:00:00', 'Scheduled'),
(1002, 2, '2025-05-05', '14:30:00', 'Cancelled'),
(1001, 4, '2025-05-06', '11:15:00', 'Scheduled');

-- ------------------------------------------------------
-- INSERT PAYMENTS
-- ------------------------------------------------------
INSERT INTO Payment (appointmentID, amount, method) VALUES
(1, 50.00, 'Cash'),
(2, 150.00, 'CreditCard'),
(3, 80.00, 'Cash'),
(4, 120.00, 'CreditCard'),
(5, 40.00, 'Cash');

-- ------------------------------------------------------
-- CASH PAYMENTS
-- ------------------------------------------------------
INSERT INTO Cash (cashID, receivedBy) VALUES
(1, 'Layla'),
(3, 'Layla'),
(5, 'Layla');

-- ------------------------------------------------------
-- CREDIT CARD PAYMENTS
-- ------------------------------------------------------
INSERT INTO CreditCard (creditID, cvv_num, cardNumber, expiryDate) VALUES
(2, '223', '4111111111111111', '2026-12-01'),
(4, '897', '4222222222222', '2025-08-01');

-- ------------------------------------------------------
-- FEEDBACK
-- ------------------------------------------------------
INSERT INTO Feedback (customerID, serviceID, rating, comments) VALUES
(1001, 1, 5, 'Excellent haircut!'),
(1001, 5, 4, 'Makeup was very nice.'),
(1002, 3, 3, 'Facial was okay, a bit rushed.'),
(1002, 2, 2, 'Hair color not what I expected.'),
(1001, 4, 5, 'Loved the manicure!');

-- ------------------------------------------------------
-- CHECK USERS LIST
-- ------------------------------------------------------
SELECT userID, username, email FROM User;