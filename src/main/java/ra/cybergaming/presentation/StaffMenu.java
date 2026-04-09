package ra.cybergaming.presentation;

import java.util.Scanner;

public class StaffMenu {
    private Scanner scanner;

    public StaffMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu() {
        boolean isRunning = true;
        
        while (isRunning) {
            System.out.println("\n======================================================================");
            System.out.println("|                    MENU NHÂN VIÊN - STAFF                        |");
            System.out.println("======================================================================");
            System.out.println("  1. Xem danh sách đơn hàng");
            System.out.println("  2. Xem & Tiếp nhận đơn hàng đang pending");
            System.out.println("  3. Cập nhật trạng thái đơn hàng");
            System.out.println("  4. Xác nhận khách hàng đã nhận máy");
            System.out.println("  5. Cập nhật trạng thái máy");
            System.out.println("  0. Đăng xuất");
            System.out.println("======================================================================");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        System.out.println("Bạn chọn: Xem danh sách đơn hàng");

                        break;
                    case 2:
                        System.out.println("Bạn chọn: Xem & Tiếp nhận đơn hàng đang pending");

                        break;
                    case 3:
                        System.out.println("Bạn chọn: Cập nhật trạng thái đơn hàng");

                        break;
                    case 4:
                        System.out.println("Bạn chọn: Xác nhận khách hàng đã nhận máy");

                        break;
                    case 5:
                        System.out.println("Bạn chọn: Cập nhật trạng thái máy");

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
