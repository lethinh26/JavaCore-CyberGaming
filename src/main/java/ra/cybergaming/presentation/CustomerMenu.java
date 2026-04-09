package ra.cybergaming.presentation;

import java.util.Scanner;

public class CustomerMenu {
    private Scanner scanner;

    public CustomerMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu() {
        boolean isRunning = true;
        
        while (isRunning) {
            System.out.println("\n======================================================================");
            System.out.println("|                   MENU KHÁCH HÀNG - CUSTOMER                      |");
            System.out.println("======================================================================");
            System.out.println("  1. Xem thông tin tài khoản");
            System.out.println("  2. Đặt trước máy trạm theo khu vực");
            System.out.println("  3. Đặt đồ ăn/thức uống");
            System.out.println("  4. Xem lịch sử giao dịch");
            System.out.println("  0. Đăng xuất");
            System.out.println("======================================================================");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        System.out.println("Bạn chọn: Xem thông tin tài khoản");

                        break;
                    case 2:
                        System.out.println("Bạn chọn: Đặt trước máy trạm theo khu vực");

                        break;
                    case 3:
                        System.out.println("Bạn chọn: Đặt đồ ăn/thức uống");

                        break;
                    case 4:
                        System.out.println("Bạn chọn: Xem lịch sử giao dịch");

                        break;
                    case 0:
                        System.out.println("Đăng xuất...");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Lỗi: Vui lòng chọn một option hợp lệ!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập một số hợp lệ!");
            }
        }
    }
}
