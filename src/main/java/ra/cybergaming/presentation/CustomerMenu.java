package ra.cybergaming.presentation;

import ra.cybergaming.service.auth.SessionManager;
import ra.cybergaming.service.customer.CustomerService;

import java.util.Scanner;

public class CustomerMenu {
    private final Scanner scanner;
    private final CustomerService customerService;

    public CustomerMenu(Scanner scanner) {
        this.scanner = scanner;
        this.customerService = new CustomerService();
    }

    public void showMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "MENU KHÁCH HÀNG");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Xem thông tin tài khoản");
            System.out.printf("| %-36s |\n", "2. Đặt trước máy trạm");
            System.out.printf("| %-36s |\n", "3. Đặt đồ ăn, thức uống");
            System.out.printf("| %-36s |\n", "4. Xem lịch sử giao dịch");
            System.out.printf("| %-36s |\n", "0. Đăng xuất");
            System.out.println("+======================================+");
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
                        SessionManager.logout();
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
            System.out.println("\n+======================================+");
            System.out.printf("| %-38s |\n", "ĐẶT TRƯỚC MÁY TRẠM");
            System.out.println("+======================================+");
            System.out.printf("| %-38s |\n", "1. Đặt máy trạm");
            System.out.printf("| %-38s |\n", "2. Hủy đặt máy trạm");
            System.out.printf("| %-38s |\n", "3. Đóng máy trạm");
            System.out.printf("| %-38s |\n", "4. Xem máy chưa thanh toán");
            System.out.printf("| %-38s |\n", "5. Xem lịch sử đặt máy");
            System.out.printf("| %-38s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        System.out.println("Bạn chọn: Đặt máy trạm");
                        customerService.bookWorkstation();
                        break;
                    case 2:
                        System.out.println("Bạn chọn: Hủy đặt máy trạm");
                        customerService.cancelBooking();
                        break;
                    case 3:
                        System.out.println("Bạn chọn: Đóng máy trạm");
                        customerService.closeWorkstation();
                        break;
                    case 4:
                        System.out.println("Bạn chọn: Xem máy chưa thanh toán");
                        customerService.viewUnpaidWorkstations();
                        break;
                    case 5:
                        System.out.println("Bạn chọn: Xem lịch sử đặt máy");
                        customerService.viewBookingHistory();
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
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "ĐẶT ĐỒ ĂN, THỨC UỐNG");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Đặt món");
            System.out.printf("| %-36s |\n", "2. Xem trạng thái đơn hàng");
            System.out.printf("| %-36s |\n", "3. Hủy đơn hàng");
            System.out.printf("| %-36s |\n", "4. Lịch sử đơn hàng");
            System.out.printf("| %-36s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        System.out.println("Bạn chọn: Đặt món");
                        customerService.orderFood();
                        break;
                    case 2:
                        System.out.println("Bạn chọn: Xem trạng thái đơn hàng");
                        customerService.viewOrderStatus();
                        break;
                    case 3:
                        System.out.println("Bạn chọn: Hủy đơn hàng");
                        customerService.cancelOrder();
                        break;
                    case 4:
                        System.out.println("Bạn chọn: Lịch sử đơn hàng");
                        customerService.viewOrderHistory();
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