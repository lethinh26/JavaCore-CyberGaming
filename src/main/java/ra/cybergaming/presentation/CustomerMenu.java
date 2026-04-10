package ra.cybergaming.presentation;

import ra.cybergaming.service.customer.CustomerService;

import java.util.Scanner;

public class CustomerMenu {
    private final Scanner scanner;
    private final CustomerService customerService;

    public CustomerMenu(Scanner scanner) {
        this.scanner = scanner;
        customerService = CustomerService.getInstance();
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
                        customerService.showInfo();
                        break;
                    case 2:
                        workstationMenu();
                        break;
                    case 3:
                        serviceMenu();
                        break;
                    case 4:
                        customerService.viewTransactionHistory();
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

    public void workstationMenu() {
        boolean isRunning = true;
        
        while (isRunning) {
            System.out.println("\n======================================================================");
            System.out.println("|                   ĐẶT TRƯỚC MÁY TRẠM                             |");
            System.out.println("======================================================================");
            System.out.println("  1. Đặt máy trạm");
            System.out.println("  2. Huỷ đặt máy trạm");
            System.out.println("  3. Xem danh sách đã đặt");
            System.out.println("  4. Xem danh sách đã huỷ");
            System.out.println("  0. Quay lại");
            System.out.println("======================================================================");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        System.out.println("Bạn chọn: Đặt máy trạm");
                        customerService.bookWorkstation();
                        break;
                    case 2:
                        System.out.println("Bạn chọn: Huỷ đặt máy trạm");
                        customerService.cancelBooking();
                        break;
                    case 3:
                        System.out.println("Bạn chọn: Xem danh sách đã đặt");
                        customerService.viewPendingBookings();
                        break;
                    case 4:
                        System.out.println("Bạn chọn: Xem danh sách đã huỷ");
                        customerService.viewCancelledBookings();
                        break;
                    case 0:
                        System.out.println("Quay lại menu chính...");
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

    public void serviceMenu() {
        boolean isRunning = true;
        
        while (isRunning) {
            System.out.println("\n======================================================================");
            System.out.println("|                   ĐẶT ĐỒ ĂN / THỨC UỐNG                         |");
            System.out.println("======================================================================");
            System.out.println("  1. Thêm đồ ăn/thức uống vào giỏ");
            System.out.println("  2. Huỷ đơn hàng");
            System.out.println("  3. Xem danh sách đơn hàng đang chờ");
            System.out.println("  4. Xem danh sách đã hủy");
            System.out.println("  0. Quay lại");
            System.out.println("======================================================================");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        System.out.println("Bạn chọn: Đặt đồ ăn/thức uống");
                        customerService.orderFood();
                        break;
                    case 2:
                        System.out.println("Bạn chọn: Huỷ đơn hàng");
                        customerService.cancelOrder();
                        break;
                    case 3:
                        System.out.println("Bạn chọn: Xem danh sách đơn hàng đang chờ");
                        customerService.viewPendingOrders();
                        break;
                    case 4:
                        System.out.println("Bạn chọn: Xem danh sách đã hủy");
                        customerService.viewCancelledOrders();
                        break;
                    case 0:
                        System.out.println("Quay lại menu chính...");
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
