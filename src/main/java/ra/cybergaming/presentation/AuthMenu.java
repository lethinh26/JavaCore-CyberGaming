package ra.cybergaming.presentation;

import ra.cybergaming.service.auth.AuthService;
import ra.cybergaming.service.auth.SessionManager;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class AuthMenu {
    private final Scanner scanner;
    private final AuthService authService;

    public AuthMenu(Scanner scanner) {
        this.scanner = scanner;
        authService = AuthService.getInstance();
    }

    public void showMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "XÁC THỰC NGƯỜI DÙNG");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Đăng ký");
            System.out.printf("| %-36s |\n", "2. Đăng nhập");
            System.out.printf("| %-36s |\n", "0. Thoát");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                if (!scanner.hasNextLine()) {
                    System.out.println("Không có input từ stdin. Thoát...");
                    break;
                }

                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        authService.register();
                        break;
                    case 2:
                        authService.login();
                        if (SessionManager.isLoggedIn()) {
                            System.out.println("Chào mừng " + SessionManager.getCurrentUser().getFullName() + "!");
                            isRunning = false;
                        }
                        break;
                    case 0:
                        System.out.println("Thoát chương trình...");
                        isRunning = false;
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Lỗi: Vui lòng chọn một option hợp lệ!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập một số hợp lệ!");
            } catch (NoSuchElementException e) {
                System.out.println("Lỗi: Không có input. Thoát...");
                break;
            }
        }
    }
}