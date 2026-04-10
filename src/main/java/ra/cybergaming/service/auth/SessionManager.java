package ra.cybergaming.service.auth;

import ra.cybergaming.model.User;
import ra.cybergaming.model.enums.RoleType;

public class SessionManager {
    public static User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        SessionManager.currentUser = currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
        System.out.println("Đã đăng xuất thành công!");
    }

    public static RoleType getUserRole() {
        if (currentUser == null) {
            return null;
        }
        return currentUser.getRoleType();
    }

    public static boolean hasRole(RoleType role) {
        return isLoggedIn() && currentUser.getRoleType() == role;
    }
}
