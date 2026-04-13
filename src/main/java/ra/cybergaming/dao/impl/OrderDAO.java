package ra.cybergaming.dao.impl;

import ra.cybergaming.dao.IBaseDAO;
import ra.cybergaming.model.Order;
import ra.cybergaming.model.enums.OrderStatus;
import ra.cybergaming.util.DBConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO implements IBaseDAO<Order> {

    @Override
    public boolean create(Order entity) {
        if (entity.getBookingId() < 0) {
            System.out.println("Lỗi: ID đặt máy không hợp lệ.");
            return false;
        }

        if (entity.getCustomerId() < 0) {
            System.out.println("Lỗi: ID khách hàng không hợp lệ.");
            return false;
        }

        String sql = "INSERT INTO orders (booking_id, customer_id, staff_id, note, order_status, total_amount, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, entity.getBookingId());
            pstmt.setInt(2, entity.getCustomerId());
            if (entity.getStaffId() > 0) {
                pstmt.setInt(3, entity.getStaffId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setString(4, entity.getNote());
            pstmt.setString(5, OrderStatus.PENDING.toString());
            pstmt.setDouble(6, entity.getTotalAmount());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);
                    entity.setOrderId(orderId);
                    entity.setOrderCode(generateOrderCode(orderId));
                    System.out.println("Tạo đơn hàng thành công! Mã đơn: " + entity.getOrderCode());
                }
                return true;
            }
            return false;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tạo đơn hàng - " + e.getMessage());
            return false;
        }
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy đơn hàng - " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";

        try (Connection conn = DBConnector.openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách đơn hàng - " + e.getMessage());
        }

        return orders;
    }

    @Override
    public boolean update(Order entity) {
        Order existingOrder = findById(entity.getOrderId());
        if (existingOrder == null) {
            System.out.println("Lỗi: Đơn hàng không tồn tại.");
            return false;
        }

        String sql = "UPDATE orders SET order_status = ?, total_amount = ?, staff_id = ?, note = ?, updated_at = ? WHERE order_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getOrderStatus().toString());
            pstmt.setDouble(2, entity.getTotalAmount());
            if (entity.getStaffId() > 0) {
                pstmt.setInt(3, entity.getStaffId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setString(4, entity.getNote());
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(6, entity.getOrderId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể cập nhật đơn hàng - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        Order order = findById(id);
        if (order == null) {
            System.out.println("Lỗi: Đơn hàng không tồn tại.");
            return false;
        }

        String sql = "UPDATE orders SET order_status = 'CANCELLED', updated_at = ? WHERE order_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                System.out.println("Hủy đơn hàng thành công!");
            }
            return result;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể hủy đơn hàng - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Order> search(String keyword) {
        List<Order> result = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE order_id LIKE ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tìm kiếm đơn hàng - " + e.getMessage());
        }

        return result;
    }

    public List<Order> findByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách đơn hàng của khách - " + e.getMessage());
        }

        return orders;
    }

    public List<Order> findByBookingId(int bookingId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE booking_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách đơn hàng theo đặt máy - " + e.getMessage());
        }

        return orders;
    }

    public boolean updateOrderTotal(int orderId) {
        String sql = "UPDATE orders SET total_amount = (SELECT COALESCE(SUM(line_total), 0) FROM order_items WHERE order_id = ?), updated_at = ? WHERE order_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, orderId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể cập nhật tổng tiền đơn hàng - " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE orders SET order_status = ?, updated_at = ? WHERE order_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.toString());
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, orderId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể cập nhật trạng thái đơn hàng - " + e.getMessage());
            return false;
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        int orderId = rs.getInt("order_id");
        order.setOrderId(orderId);
        order.setOrderCode(generateOrderCode(orderId));
        order.setBookingId(rs.getInt("booking_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setStaffId(rs.getInt("staff_id"));
        order.setNote(rs.getString("note"));
        order.setOrderStatus(OrderStatus.valueOf(rs.getString("order_status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt();
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            order.setUpdatedAt();
        }

        return order;
    }

    private String generateOrderCode(int orderId) {
        return "OD" + String.format("%05d", orderId);
    }
}
