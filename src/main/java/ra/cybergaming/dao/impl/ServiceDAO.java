package ra.cybergaming.dao.impl;

import ra.cybergaming.dao.IBaseDAO;
import ra.cybergaming.model.Service;
import ra.cybergaming.model.enums.CategoryType;
import ra.cybergaming.model.enums.ServiceStatus;
import ra.cybergaming.util.DBConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO implements IBaseDAO<Service> {

    @Override
    public boolean create(Service entity) {
        if (entity.getServiceName() == null || entity.getServiceName().isEmpty()) {
            System.out.println("Lỗi: Tên dịch vụ không được để trống.");
            return false;
        }

        if (entity.getPrice() < 0) {
            System.out.println("Lỗi: Giá dịch vụ không được nhỏ hơn 0.");
            return false;
        }

        String sql = "INSERT INTO services (service_name, category, description, price, stock_quantity, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, entity.getServiceName());
            pstmt.setString(2, entity.getCategory().toString());
            pstmt.setString(3, entity.getDescription());
            pstmt.setDouble(4, entity.getPrice());
            pstmt.setInt(5, entity.getStock_quantity());
            pstmt.setString(6, ServiceStatus.ACTIVE.toString());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int serviceId = rs.getInt(1);
                    entity.setServiceId(serviceId);
                    entity.setServiceCode(generateServiceCode(serviceId));
                    System.out.println("Thêm dịch vụ thành công! Mã dịch vụ: " + entity.getServiceCode());
                }
                return true;
            }
            return false;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tạo dịch vụ - " + e.getMessage());
            return false;
        }
    }

    @Override
    public Service findById(int id) {
        String sql = "SELECT * FROM services WHERE service_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToService(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy dịch vụ - " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Service> findAll() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE status = 'ACTIVE' AND stock_quantity > 0";

        try (Connection conn = DBConnector.openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách dịch vụ - " + e.getMessage());
        }

        return services;
    }

    @Override
    public boolean update(Service entity) {
        Service existingService = findById(entity.getServiceId());
        if (existingService == null) {
            System.out.println("Lỗi: Dịch vụ không tồn tại.");
            return false;
        }

        String sql = "UPDATE services SET service_name = ?, category = ?, description = ?, price = ?, stock_quantity = ?, status = ?, updated_at = ? WHERE service_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getServiceName());
            pstmt.setString(2, entity.getCategory().toString());
            pstmt.setString(3, entity.getDescription());
            pstmt.setDouble(4, entity.getPrice());
            pstmt.setInt(5, entity.getStock_quantity());
            pstmt.setString(6, entity.getStatus().toString());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(8, entity.getServiceId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể cập nhật dịch vụ - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        Service service = findById(id);
        if (service == null) {
            System.out.println("Lỗi: Dịch vụ không tồn tại.");
            return false;
        }

        String sql = "UPDATE services SET status = 'INACTIVE', updated_at = ? WHERE service_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                System.out.println("Xóa dịch vụ thành công!");
            }
            return result;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể xóa dịch vụ - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Service> search(String keyword) {
        List<Service> result = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE service_name LIKE ? OR description LIKE ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result.add(mapResultSetToService(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tìm kiếm dịch vụ - " + e.getMessage());
        }

        return result;
    }

    public List<Service> findByCategory(CategoryType category) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE category = ? AND status = 'ACTIVE' AND stock_quantity > 0";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách dịch vụ theo danh mục - " + e.getMessage());
        }

        return services;
    }

    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        int serviceId = rs.getInt("service_id");
        service.setServiceId(serviceId);
        service.setServiceCode(generateServiceCode(serviceId));
        service.setServiceName(rs.getString("service_name"));
        service.setCategory(CategoryType.valueOf(rs.getString("category")));
        service.setDescription(rs.getString("description"));
        service.setPrice(rs.getDouble("price"));
        service.setStock_quantity(rs.getInt("stock_quantity"));
        service.setStatus(ServiceStatus.valueOf(rs.getString("status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            service.setCreatedAt();
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            service.setUpdatedAt();
        }

        return service;
    }

    private String generateServiceCode(int serviceId) {
        return "SV" + String.format("%04d", serviceId);
    }
}
