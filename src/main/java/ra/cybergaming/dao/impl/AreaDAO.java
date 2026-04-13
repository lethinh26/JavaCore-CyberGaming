package ra.cybergaming.dao.impl;

import ra.cybergaming.dao.IBaseDAO;
import ra.cybergaming.model.Area;
import ra.cybergaming.util.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO implements IBaseDAO<Area> {

    @Override
    public boolean create(Area entity) {
        if (entity.getAreaName() == null || entity.getAreaName().isEmpty()) {
            System.out.println("Lỗi: Tên phòng không được để trống.");
            return false;
        }

        String sql = "INSERT INTO areas (area_name, description) VALUES (?, ?)";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, entity.getAreaName());
            pstmt.setString(2, entity.getDescription());

            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int areaId = rs.getInt(1);
                    entity.setAreaId(areaId);
                    System.out.println("Tạo phòng máy thành công! ID: " + areaId);
                }
                return true;
            }
            return false;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tạo phòng máy - " + e.getMessage());
            return false;
        }
    }

    @Override
    public Area findById(int id) {
        String sql = "SELECT * FROM areas WHERE area_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToArea(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy phòng máy - " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Area> findAll() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT * FROM areas";

        try (Connection conn = DBConnector.openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                areas.add(mapResultSetToArea(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách phòng máy - " + e.getMessage());
        }

        return areas;
    }

    @Override
    public boolean update(Area entity) {
        Area existingArea = findById(entity.getAreaId());
        if (existingArea == null) {
            System.out.println("Lỗi: Phòng máy không tồn tại.");
            return false;
        }

        String sql = "UPDATE areas SET area_name = ?, description = ? WHERE area_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getAreaName());
            pstmt.setString(2, entity.getDescription());
            pstmt.setInt(3, entity.getAreaId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể cập nhật phòng máy - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        Area area = findById(id);
        if (area == null) {
            System.out.println("Lỗi: Phòng máy không tồn tại.");
            return false;
        }

        String sql = "DELETE FROM areas WHERE area_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể xóa phòng máy - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Area> search(String keyword) {
        List<Area> result = new ArrayList<>();
        String sql = "SELECT * FROM areas WHERE area_name LIKE ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result.add(mapResultSetToArea(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tìm kiếm phòng máy - " + e.getMessage());
        }

        return result;
    }

    private Area mapResultSetToArea(ResultSet rs) throws SQLException {
        Area area = new Area();
        area.setAreaId(rs.getInt("area_id"));
        area.setAreaName(rs.getString("area_name"));
        area.setDescription(rs.getString("description"));

        return area;
    }
}
