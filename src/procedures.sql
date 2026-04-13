DELIMITER %%

DROP PROCEDURE IF EXISTS CreateBooking%%

CREATE PROCEDURE CreateBooking(
    IN p_customer_id INT,
    IN p_workstation_id INT,
    IN p_start_time DATETIME,
    OUT p_booking_id INT,
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(255)
)
proc_label: BEGIN
    DECLARE v_customer_exists INT DEFAULT 0;
    DECLARE v_workstation_exists INT DEFAULT 0;
    DECLARE v_workstation_available INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Lỗi database: Không thể tạo đặt máy';
        SET p_booking_id = -1;
    END;

    START TRANSACTION;

    SELECT COUNT(*) INTO v_customer_exists
    FROM users WHERE user_id = p_customer_id AND role_id = 3;
    
    IF v_customer_exists = 0 THEN
        ROLLBACK;
        SET p_status = 'INVALID_CUSTOMER';
        SET p_message = 'Khách hàng không tồn tại hoặc không hợp lệ';
        SET p_booking_id = -1;
        LEAVE proc_label;
    END IF;

    SELECT COUNT(*) INTO v_workstation_exists
    FROM workstations WHERE workstation_id = p_workstation_id;
    
    IF v_workstation_exists = 0 THEN
        ROLLBACK;
        SET p_status = 'WORKSTATION_NOT_FOUND';
        SET p_message = 'Máy trạm không tồn tại';
        SET p_booking_id = -1;
        LEAVE proc_label;
    END IF;

    SELECT COUNT(*) INTO v_workstation_available
    FROM workstations WHERE workstation_id = p_workstation_id AND status = 'AVAILABLE';
    
    IF v_workstation_available = 0 THEN
        ROLLBACK;
        SET p_status = 'WORKSTATION_NOT_AVAILABLE';
        SET p_message = 'Máy trạm không khả dụng';
        SET p_booking_id = -1;
        LEAVE proc_label;
    END IF;

    IF p_start_time <= NOW() THEN
        ROLLBACK;
        SET p_status = 'INVALID_TIME';
        SET p_message = 'Thời gian bắt đầu phải là tương lai';
        SET p_booking_id = -1;
        LEAVE proc_label;
    END IF;

    INSERT INTO bookings (customer_id, workstation_id, start_time, booking_status)
    VALUES (p_customer_id, p_workstation_id, p_start_time, 'PENDING');

    SET p_booking_id = LAST_INSERT_ID();

    COMMIT;
    SET p_status = 'SUCCESS';
    SET p_message = CONCAT('Đặt máy thành công! Mã đặt: BO', LPAD(p_booking_id, 5, '0'));

END proc_label%%

DROP PROCEDURE IF EXISTS CreateOrder%%

CREATE PROCEDURE CreateOrder(
    IN p_booking_id INT,
    IN p_customer_id INT,
    IN p_staff_id INT,
    IN p_note VARCHAR(255),
    OUT p_order_id INT,
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(255)
)
proc_label: BEGIN
    DECLARE v_booking_exists INT DEFAULT 0;
    DECLARE v_booking_customer INT DEFAULT 0;
    DECLARE v_staff_exists INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Lỗi database: Không thể tạo đơn hàng';
        SET p_order_id = -1;
    END;

    START TRANSACTION;

    SELECT COUNT(*) INTO v_booking_exists FROM bookings WHERE booking_id = p_booking_id;
    
    IF v_booking_exists = 0 THEN
        ROLLBACK;
        SET p_status = 'BOOKING_NOT_FOUND';
        SET p_message = 'Đặt máy không tồn tại';
        SET p_order_id = -1;
        LEAVE proc_label;
    END IF;

    SELECT COUNT(*) INTO v_booking_customer
    FROM bookings WHERE booking_id = p_booking_id AND customer_id = p_customer_id;
    
    IF v_booking_customer = 0 THEN
        ROLLBACK;
        SET p_status = 'INVALID_BOOKING';
        SET p_message = 'Đặt máy không thuộc về khách hàng này';
        SET p_order_id = -1;
        LEAVE proc_label;
    END IF;

    IF p_staff_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_staff_exists 
        FROM users WHERE user_id = p_staff_id AND role_id = 2;
        
        IF v_staff_exists = 0 THEN
            ROLLBACK;
            SET p_status = 'INVALID_STAFF';
            SET p_message = 'Nhân viên không tồn tại hoặc không hợp lệ';
            SET p_order_id = -1;
            LEAVE proc_label;
        END IF;
    END IF;

    INSERT INTO orders (booking_id, customer_id, staff_id, note, order_status, total_amount)
    VALUES (p_booking_id, p_customer_id, p_staff_id, p_note, 'PENDING', 0.00);

    SET p_order_id = LAST_INSERT_ID();

    COMMIT;
    SET p_status = 'SUCCESS';
    SET p_message = CONCAT('Tạo đơn hàng thành công! Mã đơn: ', p_order_id);

END proc_label%%

DROP PROCEDURE IF EXISTS AddOrderItem%%

CREATE PROCEDURE AddOrderItem(
    IN p_order_id INT,
    IN p_service_id INT,
    IN p_quantity INT,
    OUT p_order_item_id INT,
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(255),
    OUT p_new_total DECIMAL(12,2)
)
proc_label: BEGIN
    DECLARE v_unit_price DECIMAL(10,2) DEFAULT 0;
    DECLARE v_line_total DECIMAL(12,2) DEFAULT 0;
    DECLARE v_stock_quantity INT DEFAULT 0;
    DECLARE v_order_exists INT DEFAULT 0;
    DECLARE v_service_exists INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Lỗi database: Không thể thêm món vào đơn';
        SET p_order_item_id = -1;
        SET p_new_total = 0;
    END;

    START TRANSACTION;

    SELECT COUNT(*) INTO v_order_exists FROM orders WHERE order_id = p_order_id;
    
    IF v_order_exists = 0 THEN
        ROLLBACK;
        SET p_status = 'ORDER_NOT_FOUND';
        SET p_message = 'Đơn hàng không tồn tại';
        SET p_order_item_id = -1;
        SET p_new_total = 0;
        LEAVE proc_label;
    END IF;

    SELECT COUNT(*) INTO v_service_exists FROM services WHERE service_id = p_service_id;
    
    IF v_service_exists = 0 THEN
        ROLLBACK;
        SET p_status = 'SERVICE_NOT_FOUND';
        SET p_message = 'Dịch vụ không tồn tại';
        SET p_order_item_id = -1;
        SET p_new_total = 0;
        LEAVE proc_label;
    END IF;

    SELECT price, stock_quantity INTO v_unit_price, v_stock_quantity
    FROM services WHERE service_id = p_service_id;

    IF v_stock_quantity < p_quantity THEN
        ROLLBACK;
        SET p_status = 'INSUFFICIENT_STOCK';
        SET p_message = CONCAT('Không đủ hàng. Chỉ còn: ', v_stock_quantity);
        SET p_order_item_id = -1;
        SET p_new_total = 0;
        LEAVE proc_label;
    END IF;

    IF p_quantity <= 0 THEN
        ROLLBACK;
        SET p_status = 'INVALID_QUANTITY';
        SET p_message = 'Số lượng phải lớn hơn 0';
        SET p_order_item_id = -1;
        SET p_new_total = 0;
        LEAVE proc_label;
    END IF;

    SET v_line_total = v_unit_price * p_quantity;

    INSERT INTO order_items (order_id, service_id, quantity, unit_price, line_total)
    VALUES (p_order_id, p_service_id, p_quantity, v_unit_price, v_line_total);

    SET p_order_item_id = LAST_INSERT_ID();

    UPDATE orders
    SET total_amount = (SELECT SUM(line_total) FROM order_items WHERE order_id = p_order_id)
    WHERE order_id = p_order_id;

    SELECT total_amount INTO p_new_total FROM orders WHERE order_id = p_order_id;

    UPDATE services
    SET stock_quantity = stock_quantity - p_quantity
    WHERE service_id = p_service_id;

    UPDATE services
    SET status = 'OUT_OF_STOCK'
    WHERE service_id = p_service_id AND stock_quantity <= 0;

    COMMIT;
    SET p_status = 'SUCCESS';
    SET p_message = 'Thêm món vào đơn hàng thành công';

END proc_label%%

DROP PROCEDURE IF EXISTS CompleteBooking%%

CREATE PROCEDURE CompleteBooking(
    IN p_booking_id INT,
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(255),
    OUT p_total_amount DECIMAL(10,2)
)
proc_label: BEGIN
    DECLARE v_customer_id INT DEFAULT 0;
    DECLARE v_workstation_id INT DEFAULT 0;
    DECLARE v_hourly_rate DECIMAL(10,2) DEFAULT 0;
    DECLARE v_start_time DATETIME;
    DECLARE v_duration_hours DECIMAL(10,2) DEFAULT 0;
    DECLARE v_booking_total DECIMAL(10,2) DEFAULT 0;
    DECLARE v_current_balance DECIMAL(12,2) DEFAULT 0;
    DECLARE v_booking_exists INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Lỗi database: Không thể hoàn thành đặt máy';
        SET p_total_amount = 0;
    END;

    START TRANSACTION;

    SELECT COUNT(*) INTO v_booking_exists FROM bookings WHERE booking_id = p_booking_id;
    
    IF v_booking_exists = 0 THEN
        ROLLBACK;
        SET p_status = 'BOOKING_NOT_FOUND';
        SET p_message = 'Booking không tồn tại';
        SET p_total_amount = 0;
        LEAVE proc_label;
    END IF;

    SELECT customer_id, workstation_id, start_time
    INTO v_customer_id, v_workstation_id, v_start_time
    FROM bookings WHERE booking_id = p_booking_id;

    SELECT hourly_rate INTO v_hourly_rate
    FROM workstations WHERE workstation_id = v_workstation_id;

    SET v_duration_hours = CEIL(TIMESTAMPDIFF(MINUTE, v_start_time, NOW()) / 60.0);

    IF v_duration_hours < 1 THEN
        SET v_duration_hours = 1;
    END IF;

    SET v_booking_total = v_hourly_rate * v_duration_hours;

    SELECT balance INTO v_current_balance FROM users WHERE user_id = v_customer_id;

    IF v_current_balance < v_booking_total THEN
        ROLLBACK;
        SET p_status = 'INSUFFICIENT_BALANCE';
        SET p_message = CONCAT('Số dư không đủ. Cần: ', v_booking_total, ' VND');
        SET p_total_amount = 0;
        LEAVE proc_label;
    END IF;

    UPDATE bookings
    SET end_time = NOW(), 
        total_amount = v_booking_total,
        booking_status = 'COMPLETED'
    WHERE booking_id = p_booking_id;

    UPDATE users
    SET balance = balance - v_booking_total
    WHERE user_id = v_customer_id;

    UPDATE workstations
    SET status = 'AVAILABLE'
    WHERE workstation_id = v_workstation_id;

    COMMIT;
    SET p_status = 'SUCCESS';
    SET p_message = CONCAT('Hoàn thành đặt máy! Thời gian: ', v_duration_hours, ' giờ, Tiền: ', v_booking_total, ' VND');
    SET p_total_amount = v_booking_total;

END proc_label%%


DROP PROCEDURE IF EXISTS CompleteOrder%%

CREATE PROCEDURE CompleteOrder(
    IN p_order_id INT,
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(255),
    OUT p_final_total DECIMAL(12,2)
)
proc_label: BEGIN
    DECLARE v_customer_id INT DEFAULT 0;
    DECLARE v_order_total DECIMAL(12,2) DEFAULT 0;
    DECLARE v_current_balance DECIMAL(12,2) DEFAULT 0;
    DECLARE v_order_exists INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Lỗi database: Không thể hoàn thành đơn hàng';
        SET p_final_total = 0;
    END;

    START TRANSACTION;

    SELECT COUNT(*) INTO v_order_exists FROM orders WHERE order_id = p_order_id;
    
    IF v_order_exists = 0 THEN
        ROLLBACK;
        SET p_status = 'ORDER_NOT_FOUND';
        SET p_message = 'Đơn hàng không tồn tại';
        SET p_final_total = 0;
        LEAVE proc_label;
    END IF;

    SELECT customer_id, total_amount INTO v_customer_id, v_order_total
    FROM orders WHERE order_id = p_order_id;

    SELECT balance INTO v_current_balance FROM users WHERE user_id = v_customer_id;

    IF v_current_balance < v_order_total THEN
        ROLLBACK;
        SET p_status = 'INSUFFICIENT_BALANCE';
        SET p_message = CONCAT('Số dư không đủ. Cần: ', v_order_total, ' VND');
        SET p_final_total = 0;
        LEAVE proc_label;
    END IF;

    UPDATE orders 
    SET order_status = 'COMPLETED'
    WHERE order_id = p_order_id;

    UPDATE users 
    SET balance = balance - v_order_total
    WHERE user_id = v_customer_id;

    COMMIT;
    SET p_status = 'SUCCESS';
    SET p_message = 'Hoàn thành đơn hàng thành công';
    SET p_final_total = v_order_total;

END proc_label%%

DELIMITER ;
