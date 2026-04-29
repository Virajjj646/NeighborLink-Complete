-- ============================================================
--  NeighborLink Community Marketplace - Schema
--  Based on DBMS Report: Exact DDL, Constraints, Triggers, Functions
-- ============================================================

CREATE DATABASE IF NOT EXISTS neighborlink_db;
USE neighborlink_db;

-- ============================================================
-- TABLE 1: SOCIETY
-- ============================================================
CREATE TABLE IF NOT EXISTS SOCIETY (
    soc_id   INT PRIMARY KEY AUTO_INCREMENT,
    soc_name VARCHAR(255) NOT NULL,
    city     VARCHAR(100) NOT NULL,
    address  TEXT
);

-- ============================================================
-- TABLE 2: USERS (Linked to Society)
-- ============================================================
CREATE TABLE IF NOT EXISTS USERS (
    user_id     INT PRIMARY KEY AUTO_INCREMENT,
    soc_id      INT NOT NULL,
    username    VARCHAR(50) NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    flat_no     VARCHAR(20) NOT NULL,
    trust_score DECIMAL(3,2) DEFAULT 5.00,
    password    VARCHAR(255) NOT NULL,
    FOREIGN KEY (soc_id) REFERENCES SOCIETY(soc_id)
);

-- ============================================================
-- TABLE 3: PRODUCTS (Listings)
-- ============================================================
CREATE TABLE IF NOT EXISTS PRODUCTS (
    product_id    INT PRIMARY KEY AUTO_INCREMENT,
    owner_id      INT NOT NULL,
    title         VARCHAR(100) NOT NULL,
    description   TEXT,
    price_per_day DECIMAL(10,2) NOT NULL,
    item_value    DECIMAL(10,2),
    FOREIGN KEY (owner_id) REFERENCES USERS(user_id)
);

-- ============================================================
-- TABLE 4: RENTALS (Associative Entity)
-- ============================================================
CREATE TABLE IF NOT EXISTS RENTALS (
    rental_id  INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    renter_id  INT NOT NULL,
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL,
    status     ENUM('PENDING','ACTIVE','COMPLETED','CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (product_id) REFERENCES PRODUCTS(product_id),
    FOREIGN KEY (renter_id)  REFERENCES USERS(user_id)
);

-- ============================================================
-- TABLE 5: INSURANCE (One-to-One with Rental)
-- ============================================================
CREATE TABLE IF NOT EXISTS INSURANCE (
    policy_id      INT PRIMARY KEY AUTO_INCREMENT,
    rental_id      INT UNIQUE NOT NULL,
    premium_amt    DECIMAL(10,2) NOT NULL,
    coverage_limit DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (rental_id) REFERENCES RENTALS(rental_id)
);

-- ============================================================
-- TABLE 6: REVIEWS (Feedback for Rental)
-- ============================================================
CREATE TABLE IF NOT EXISTS REVIEWS (
    review_id   INT PRIMARY KEY AUTO_INCREMENT,
    rental_id   INT UNIQUE NOT NULL,
    reviewer_id INT NOT NULL,
    rating      INT CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rental_id)   REFERENCES RENTALS(rental_id),
    FOREIGN KEY (reviewer_id) REFERENCES USERS(user_id)
);

-- ============================================================
-- VIEW: user_activity (from report section 3.6)
-- ============================================================
CREATE OR REPLACE VIEW user_activity AS
    SELECT u.username, COUNT(r.rental_id) AS total_rentals
    FROM   USERS u
    LEFT JOIN RENTALS r ON u.user_id = r.renter_id
    GROUP BY u.username;

-- ============================================================
-- FUNCTION: total_rentals_by_user (from report section 3.7)
-- ============================================================
DROP FUNCTION IF EXISTS total_rentals_by_user;
DELIMITER //
CREATE FUNCTION total_rentals_by_user(uid INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE total INT;
    SELECT COUNT(*) INTO total FROM RENTALS WHERE renter_id = uid;
    RETURN total;
END //
DELIMITER ;

-- ============================================================
-- TRIGGER: check_valid_dates (from report section 3.8)
-- Fires BEFORE INSERT on RENTALS - prevents end_date <= start_date
-- ============================================================
DROP TRIGGER IF EXISTS check_valid_dates;
DELIMITER //
CREATE TRIGGER check_valid_dates
BEFORE INSERT ON RENTALS
FOR EACH ROW
BEGIN
    IF NEW.end_date <= NEW.start_date THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Invalid rental dates: end_date must be after start_date';
    END IF;
END //
DELIMITER ;

-- ============================================================
-- TRIGGER: update_trust_score_after_review
-- Automatically recalculates trust score when a review is added
-- ============================================================
DROP TRIGGER IF EXISTS update_trust_score_after_review;
DELIMITER //
CREATE TRIGGER update_trust_score_after_review
AFTER INSERT ON REVIEWS
FOR EACH ROW
BEGIN
    DECLARE owner_uid INT;
    DECLARE avg_rating DECIMAL(3,2);

    -- Find the product owner via rental → product
    SELECT p.owner_id INTO owner_uid
    FROM RENTALS r
    JOIN PRODUCTS p ON r.product_id = p.product_id
    WHERE r.rental_id = NEW.rental_id;

    -- Calculate average rating for that owner's products
    SELECT AVG(rv.rating) INTO avg_rating
    FROM REVIEWS rv
    JOIN RENTALS  rl ON rv.rental_id  = rl.rental_id
    JOIN PRODUCTS pr ON rl.product_id = pr.product_id
    WHERE pr.owner_id = owner_uid;

    -- Update trust score
    UPDATE USERS SET trust_score = avg_rating WHERE user_id = owner_uid;
END //
DELIMITER ;

-- ============================================================
-- STORED PROCEDURE: insert_user_safe  (exception handling - section 3.10)
-- ============================================================
DROP PROCEDURE IF EXISTS insert_user_safe;
DELIMITER //
CREATE PROCEDURE insert_user_safe(
    IN p_soc_id   INT,
    IN p_name     VARCHAR(50),
    IN p_email    VARCHAR(100),
    IN p_flat     VARCHAR(20),
    IN p_password VARCHAR(255)
)
BEGIN
    DECLARE duplicate_error CONDITION FOR 1062;
    DECLARE CONTINUE HANDLER FOR duplicate_error
    BEGIN
        SELECT 'Error: Duplicate Email Detected!' AS Message;
    END;
    INSERT INTO USERS(soc_id, username, email, flat_no, password)
    VALUES (p_soc_id, p_name, p_email, p_flat, p_password);
END //
DELIMITER ;

-- ============================================================
-- STORED PROCEDURE: show_products (cursor demo - section 3.9)
-- ============================================================
DROP PROCEDURE IF EXISTS show_products;
DELIMITER //
CREATE PROCEDURE show_products()
BEGIN
    DECLARE done  INT DEFAULT 0;
    DECLARE pname VARCHAR(100);
    DECLARE cur   CURSOR FOR SELECT title FROM PRODUCTS;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO pname;
        IF done = 1 THEN LEAVE read_loop; END IF;
        SELECT pname;
    END LOOP;
    CLOSE cur;
END //
DELIMITER ;
