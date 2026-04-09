package ra.cybergaming.service.auth;

import ra.cybergaming.dao.impl.UserDAO;
import ra.cybergaming.model.User;
import ra.cybergaming.util.InputHandler;

public class AuthService {
    private static final UserDAO userDAO = new UserDAO();
    public static boolean register() {
        String fullName = InputHandler.inputString("Nhập họ và tên: ");
        String username = InputHandler.inputUsername("Nhập tên tài khoản: ");
        String password = InputHandler.inputPassword("Nhập mật khẩu: ");
        String email = InputHandler.inputEmail("Nhập Email: ");
        String phone = InputHandler.inputPhone("Nhập số điện thoại: ");

        User userReg = new User(fullName, username, password, email, phone);
        return userDAO.register(userReg);
    }
}
