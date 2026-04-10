package ra.cybergaming.dao.impl;

import ra.cybergaming.dao.IBaseDAO;
import ra.cybergaming.model.User;
import ra.cybergaming.model.enums.RoleType;
import ra.cybergaming.model.enums.UserStatus;
import ra.cybergaming.util.DBConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IBaseDAO<User> {

    @Override
    public boolean create(User entity) {
        if (entity.getUsername() == null || entity.getUsername().isEmpty()) {
            System.out.println("Lỗi: Username không được để trống.");
            return false;
        }

        if (findByUsername(entity.getUsername()) != null) {
            System.out.println("Lỗi: Username đã tồn tại.");
            return false;
        }

        String sql = "INSERT INTO users (username, password_hash, email, full_name, phone, balance, role_id, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getUsername());
            pstmt.setString(2, entity.getPasswordHash());
            pstmt.setString(3, entity.getEmail());
            pstmt.setString(4, entity.getFullName());
            pstmt.setString(5, entity.getPhone());
            pstmt.setDouble(6, entity.getBalance());
            pstmt.setInt(7, entity.getRoleType().getValue());
            pstmt.setString(8, entity.getStatus().toString());
            pstmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tạo user - " + e.getMessage());
            return false;
        }
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy user - " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnector.openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể lấy danh sách user - " + e.getMessage());
        }

        return users;
    }

    @Override
    public boolean update(User entity) {
        User existingUser = findById(entity.getUserId());
        if (existingUser == null) {
            System.out.println("Lỗi: Người dùng không tồn tại.");
            return false;
        }

        String sql = "UPDATE users SET full_name = ?, phone = ?, email = ?, balance = ?, role_id = ?, status = ?, updated_at = ? WHERE user_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getFullName());
            pstmt.setString(2, entity.getPhone());
            pstmt.setString(3, entity.getEmail());
            pstmt.setDouble(4, entity.getBalance());
            pstmt.setInt(5, entity.getRoleType().getValue());
            pstmt.setString(6, entity.getStatus().toString());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(8, entity.getUserId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể cập nhật user - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        User user = findById(id);
        if (user == null) {
            System.out.println("Lỗi: Người dùng không tồn tại.");
            return false;
        }

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể xóa user - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> search(String keyword) {
        List<User> result = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? OR full_name LIKE ? OR phone LIKE ? OR email LIKE ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result.add(mapResultSetToUser(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tìm kiếm user - " + e.getMessage());
        }

        return result;
    }

    public User login(String username, String passwordHash) {
        if (username == null || username.isEmpty() || passwordHash == null || passwordHash.isEmpty()) {
            System.out.println("Lỗi: Username và password không được để trống.");
            return null;
        }

        User user = findByUsername(username);

        if (user == null) {
            System.out.println("Lỗi: Username không tồn tại.");
            return null;
        }

        if (!user.getPasswordHash().equals(passwordHash)) {
            System.out.println("Lỗi: Password không chính xác.");
            return null;
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            System.out.println("Lỗi: Tài khoản đã bị khóa.");
            return null;
        }

        System.out.println("Đăng nhập thành công!");
        return user;
    }

    public boolean register(User newUser) {
        if (newUser.getUsername() == null || newUser.getUsername().isEmpty()) {
            System.out.println("Lỗi: Username không được để trống.");
            return false;
        }

        if (newUser.getFullName() == null || newUser.getFullName().isEmpty()) {
            System.out.println("Lỗi: Họ tên không được để trống.");
            return false;
        }

        if (newUser.getPasswordHash() == null || newUser.getPasswordHash().isEmpty()) {
            System.out.println("Lỗi: Password không được để trống.");
            return false;
        }

        if (findByUsername(newUser.getUsername()) != null) {
            System.out.println("Lỗi: Username đã tồn tại.");
            return false;
        }

        if (checkEmailExists(newUser.getEmail())) {
            System.out.println("Lỗi: Email đã tồn tại.");
            return false;
        }

        if (checkPhoneExists(newUser.getPhone())) {
            System.out.println("Lỗi: SĐT đã tồn tại.");
            return false;
        }

        newUser.setRoleType(RoleType.CUSTOMER);

        if (create(newUser)) {
            System.out.println("Đăng ký thành công!");
            return true;
        }

        return false;
    }

    private User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnector.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể tìm username - " + e.getMessage());
        }

        return null;
    }

    private boolean checkEmailExists(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnector.openConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể kiểm tra email tồn tại: " + e.getMessage());
            return false;
        }
    }

    public boolean checkUsernameExists(String username) {
        return findByUsername(username) != null;
    }

    public boolean checkPhoneExists(String phone) {
        String sql = "SELECT * FROM users WHERE phone = ?";
        try (Connection conn = DBConnector.openConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi: Không thể kiểm tra SĐT tồn tại: " + e.getMessage());
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setBalance(rs.getDouble("balance"));
        user.setRoleType(RoleType.getByValue(rs.getInt("role_id")));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt();
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt();
        }

        return user;
    }
}
