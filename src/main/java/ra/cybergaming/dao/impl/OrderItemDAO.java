package ra.cybergaming.dao.impl;

import ra.cybergaming.model.OrderItem;
import ra.cybergaming.util.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    public boolean create(OrderItem entity) {
        String sql = "INSERT INTO order_items (order_id, service_id, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, entity.getOrderId());
            pstmt.setInt(2, entity.getServiceId());
            pstmt.setInt(3, entity.getQuantity());
            pstmt.setDouble(4, entity.getUnitPrice());
            pstmt.setDouble(5, entity.getLineTotal());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tạo order item - " + e.getMessage());
            return false;
        }
    }

    public List<OrderItem> findByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setServiceId(rs.getInt("service_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setLineTotal(rs.getDouble("line_total"));
                items.add(item);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy order items - " + e.getMessage());
        }

        return items;
    }

    public boolean deleteByOrderId(int orderId) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể xóa order items - " + e.getMessage());
            return false;
        }
    }
}
