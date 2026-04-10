package ra.cybergaming.dao.impl;

import ra.cybergaming.dao.IBaseDAO;
import ra.cybergaming.model.Workstation;
import ra.cybergaming.model.enums.WorkingStationStatus;
import ra.cybergaming.util.DBConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkstationDAO implements IBaseDAO<Workstation> {
    
    @Override
    public boolean create(Workstation entity) {
        if (entity.getStationName() == null || entity.getStationName().isEmpty()) {
            System.out.println("Lỗi: Tên máy trạm không được để trống.");
            return false;
        }

        if (entity.getSpecification() == null || entity.getSpecification().isEmpty()) {
            System.out.println("Lỗi: Thông số máy không được để trống.");
            return false;
        }

        if (entity.getHourlyRate() < 0) {
            System.out.println("Lỗi: Giá tiền mỗi giờ phải lớn hơn hoặc bằng 0.");
            return false;
        }

        String sql = "INSERT INTO workstations (station_name, area_id, specification, hourly_rate, status, note, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getStationName());
            pstmt.setInt(2, entity.getAreaId());
            pstmt.setString(3, entity.getSpecification());
            pstmt.setDouble(4, entity.getHourlyRate());
            pstmt.setString(5, WorkingStationStatus.AVAILABLE.toString());
            pstmt.setString(6, entity.getNote());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            return pstmt.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tạo máy mới - " + e.getMessage());
            return false;
        }
    }

    @Override
    public Workstation findById(int id) {
        String sql = "SELECT * FROM workstations WHERE workstation_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToWorkstation(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy máy trạm - " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Workstation> findAll() {
        List<Workstation> workstations = new ArrayList<>();
        String sql = "SELECT * FROM workstations WHERE status = 'AVAILABLE'";

        try (Connection conn = DBConnector.openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                workstations.add(mapResultSetToWorkstation(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách máy trạm - " + e.getMessage());
        }

        return workstations;
    }

    @Override
    public boolean update(Workstation entity) {
        Workstation existingWorkstation = findById(entity.getWorkstationId());
        if (existingWorkstation == null) {
            System.out.println("Lỗi: Máy trạm không tồn tại.");
            return false;
        }

        String sql = "UPDATE workstations SET station_name = ?, area_id = ?, specification = ?, hourly_rate = ?, status = ?, note = ?, updated_at = ? WHERE workstation_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getStationName());
            pstmt.setInt(2, entity.getAreaId());
            pstmt.setString(3, entity.getSpecification());
            pstmt.setDouble(4, entity.getHourlyRate());
            pstmt.setString(5, entity.getStatus().toString());
            pstmt.setString(6, entity.getNote());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(8, entity.getWorkstationId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể cập nhật máy trạm - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        Workstation workstation = findById(id);
        if (workstation == null) {
            System.out.println("Lỗi: Máy trạm không tồn tại.");
            return false;
        }

        String sql = "DELETE FROM workstations WHERE workstation_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể xoá máy trạm - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Workstation> search(String keyword) {
        List<Workstation> result = new ArrayList<>();
        String sql = "SELECT * FROM workstations WHERE station_name LIKE ? OR specification LIKE ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result.add(mapResultSetToWorkstation(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tìm kiếm máy trạm - " + e.getMessage());
        }

        return result;
    }

    private Workstation mapResultSetToWorkstation(ResultSet rs) throws SQLException {
        Workstation workstation = new Workstation();
        int workstationId = rs.getInt("workstation_id");
        workstation.setWorkstationId(workstationId);
        workstation.setStationCode(generateStationCode(workstationId));
        workstation.setStationName(rs.getString("station_name"));
        workstation.setAreaId(rs.getInt("area_id"));
        workstation.setSpecification(rs.getString("specification"));
        workstation.setHourlyRate(rs.getDouble("hourly_rate"));
        workstation.setStatus(WorkingStationStatus.valueOf(rs.getString("status")));
        workstation.setNote(rs.getString("note"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            workstation.setCreatedAt();
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            workstation.setUpdatedAt();
        }

        return workstation;
    }

    private String generateStationCode(int workstationId) {
        return "BK" + String.format("%03d", workstationId);
    }
}
