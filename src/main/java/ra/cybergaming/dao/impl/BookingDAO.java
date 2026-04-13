package ra.cybergaming.dao.impl;

import ra.cybergaming.dao.IBaseDAO;
import ra.cybergaming.model.Booking;
import ra.cybergaming.model.enums.BookingStatus;
import ra.cybergaming.model.enums.PaymentStatus;
import ra.cybergaming.util.DBConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO implements IBaseDAO<Booking> {

    @Override
    public boolean create(Booking entity) {
        if (entity.getCustomerId() < 0) {
            System.out.println("Lỗi: ID khách hàng không hợp lệ.");
            return false;
        }

        if (entity.getWorkstationId() < 0) {
            System.out.println("Lỗi: ID máy trạm không hợp lệ.");
            return false;
        }

        if (entity.getStartTime() == null) {
            System.out.println("Lỗi: Thời gian bắt đầu không được để trống.");
            return false;
        }

        String sql = "INSERT INTO bookings (customer_id, workstation_id, staff_id, start_time, booking_status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, entity.getCustomerId());
            pstmt.setInt(2, entity.getWorkstationId());
            if (entity.getStaffId() != null) {
                pstmt.setInt(3, entity.getStaffId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setTimestamp(4, Timestamp.valueOf(entity.getStartTime()));
            pstmt.setString(5, BookingStatus.PENDING.toString());
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int bookingId = rs.getInt(1);
                    entity.setBookingId(bookingId);
                    entity.setBookingCode(generateBookingCode(bookingId));
                    System.out.println("Đặt máy trạm thành công! Mã đặt: " + entity.getBookingCode());
                }
                return true;
            }
            return false;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tạo đặt máy - " + e.getMessage());
            return false;
        }
    }

    @Override
    public Booking findById(int id) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToBooking(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy đặt máy - " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";

        try (Connection conn = DBConnector.openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách đặt máy - " + e.getMessage());
        }

        return bookings;
    }

    @Override
    public boolean update(Booking entity) {
        Booking existingBooking = findById(entity.getBookingId());
        if (existingBooking == null) {
            System.out.println("Lỗi: Đặt máy không tồn tại.");
            return false;
        }

        String sql = "UPDATE bookings SET staff_id = ?, end_time = ?, total_amount = ?, booking_status = ?, payment_status = ?, updated_at = ? WHERE booking_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (entity.getStaffId() != null) {
                pstmt.setInt(1, entity.getStaffId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setTimestamp(2, entity.getEndTime() != null ? Timestamp.valueOf(entity.getEndTime()) : null);
            pstmt.setDouble(3, entity.getTotalAmount());
            pstmt.setString(4, entity.getBookingStatus().toString());
            pstmt.setString(5, entity.getPaymentStatus() != null ? entity.getPaymentStatus().toString() : null);
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(7, entity.getBookingId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể cập nhật đặt máy - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        Booking booking = findById(id);
        if (booking == null) {
            System.out.println("Lỗi: Đặt máy không tồn tại.");
            return false;
        }

        String sql = "DELETE FROM bookings WHERE booking_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể xoá đặt máy - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Booking> search(String keyword) {
        List<Booking> result = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE booking_status LIKE ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tìm kiếm đặt máy - " + e.getMessage());
        }

        return result;
    }

    public List<Booking> findByCustomerId(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách đặt máy của khách - " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking> findByCustomerIdAndStatus(int customerId, BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer_id = ? AND booking_status = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            pstmt.setString(2, status.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách đặt máy theo trạng thái - " + e.getMessage());
        }

        return bookings;
    }

    public boolean cancelBooking(int bookingId) {
        Booking booking = findById(bookingId);
        if (booking == null) {
            System.out.println("Lỗi: Đặt máy không tồn tại.");
            return false;
        }

        String sql = "UPDATE bookings SET booking_status = 'CANCELLED', updated_at = ? WHERE booking_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, bookingId);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                System.out.println("Huỷ đặt máy thành công!");
            }
            return result;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể huỷ đặt máy - " + e.getMessage());
            return false;
        }
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        int bookingId = rs.getInt("booking_id");
        booking.setBookingId(bookingId);
        booking.setBookingCode(generateBookingCode(bookingId));
        booking.setCustomerId(rs.getInt("customer_id"));
        booking.setWorkstationId(rs.getInt("workstation_id"));

        int staffId = rs.getInt("staff_id");
        if (!rs.wasNull()) {
            booking.setStaffId(staffId);
        }

        Timestamp startTime = rs.getTimestamp("start_time");
        if (startTime != null) {
            booking.setStartTime(startTime.toLocalDateTime());
        }

        Timestamp endTime = rs.getTimestamp("end_time");
        if (endTime != null) {
            booking.setEndTime(endTime.toLocalDateTime());
        }

        double totalAmount = rs.getDouble("total_amount");
        if (totalAmount > 0) {
            booking.setTotalAmount(totalAmount);
        }

        booking.setBookingStatus(BookingStatus.valueOf(rs.getString("booking_status")));
        
        String paymentStatus = rs.getString("payment_status");
        if (paymentStatus != null) {
            booking.setPaymentStatus(PaymentStatus.valueOf(paymentStatus));
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            booking.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            booking.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return booking;
    }

    private String generateBookingCode(int bookingId) {
        return "BO" + String.format("%05d", bookingId);
    }
}
