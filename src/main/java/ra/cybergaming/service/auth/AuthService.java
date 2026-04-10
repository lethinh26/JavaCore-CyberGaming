package ra.cybergaming.service.auth;

import ra.cybergaming.dao.impl.UserDAO;
import ra.cybergaming.model.User;
import ra.cybergaming.util.InputHandler;

public class AuthService {
    private static final UserDAO userDAO = new UserDAO();
    public static void register() {
        String fullName = InputHandler.inputString("Nhập họ và tên: ");
        String username = InputHandler.inputUsername("Nhập tên tài khoản: ");
        String password = InputHandler.inputPassword("Nhập mật khẩu: ");
        String email = InputHandler.inputEmail("Nhập Email: ");
        String phone = InputHandler.inputPhone("Nhập số điện thoại: ");

        User userReg = new User(fullName, username, password, email, phone);
        if (userDAO.register(userReg)) {
            SessionManager.setCurrentUser(userReg);
        }else {
            System.out.println("Đăng ký thất bại");
        }
    }

    public static void login() {
        String username = InputHandler.inputUsername("Nhập tên tài khoản: ");
        String password = InputHandler.inputPassword("Nhập mật khẩu: ");

        User user = userDAO.login(username, password);

        if  (user != null) {
            SessionManager.setCurrentUser(user);
        }
    }
}
