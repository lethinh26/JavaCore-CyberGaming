package ra.cybergaming.presentation;

import ra.cybergaming.service.staff.StaffService;
import ra.cybergaming.util.InputHandler;

import java.util.Scanner;

public class StaffMenu {
    private Scanner scanner;
    private StaffService staffService;

    public StaffMenu(Scanner scanner) {
        this.scanner = scanner;
        this.staffService = new StaffService();
    }

    public void showMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "MENU NHÂN VIÊN");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Quản lý máy trạm");
            System.out.printf("| %-36s |\n", "2. Quản lý đồ ăn/thức uống");
            System.out.printf("| %-36s |\n", "0. Đăng xuất");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        workstationManagementMenu();
                        break;
                    case 2:
                        foodDrinkManagementMenu();
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

    private void workstationManagementMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "QUẢN LÝ MÁY TRẠM");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Tiếp nhận booking pending");
            System.out.printf("| %-36s |\n", "2. Cập nhật trạng thái booking");
            System.out.printf("| %-36s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        staffService.acceptPendingBooking();
                        break;
                    case 2:
                        staffService.updateBookingStatus();
                        break;
                    case 0:
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

    private void foodDrinkManagementMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "QUẢN LÝ ĐỒ ĂN/THỨC UỐNG");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Tiếp nhận order pending");
            System.out.printf("| %-36s |\n", "2. Cập nhật trạng thái order");
            System.out.printf("| %-36s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        staffService.acceptPendingOrder();
                        break;
                    case 2:
                        staffService.updateOrderStatus();
                        break;
                    case 0:
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