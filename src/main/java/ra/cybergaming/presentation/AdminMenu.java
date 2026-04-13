package ra.cybergaming.presentation;

import ra.cybergaming.service.admin.AdminService;
import ra.cybergaming.util.InputHandler;

import java.util.Scanner;

public class AdminMenu {
    private Scanner scanner;
    private AdminService adminService;

    public AdminMenu(Scanner scanner) {
        this.scanner = scanner;
        this.adminService = new AdminService();
    }

    public void showMainMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "MENU QUẢN LÝ - ADMIN");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Quản lý danh sách máy trạm");
            System.out.printf("| %-36s |\n", "2. Quản lý phòng máy");
            System.out.printf("| %-36s |\n", "3. Quản lý menu đồ ăn, thức uống");
            System.out.printf("| %-36s |\n", "4. Quản lý người dùng");
            System.out.printf("| %-36s |\n", "5. Xuất báo cáo");
            System.out.printf("| %-36s |\n", "0. Đăng xuất");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        showComputerStationMenu();
                        break;
                    case 2:
                        showComputerRoomMenu();
                        break;
                    case 3:
                        showFoodMenuManagement();
                        break;
                    case 4:
                        showUserManagementMenu();
                        break;
                    case 5:
                        showReportMenu();
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

    public void showComputerStationMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "QUẢN LÝ MÁY TRẠM");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Xem danh sách máy trạm");
            System.out.printf("| %-36s |\n", "2. Thêm máy trạm mới");
            System.out.printf("| %-36s |\n", "3. Cập nhật máy trạm");
            System.out.printf("| %-36s |\n", "4. Xóa máy trạm");
            System.out.printf("| %-36s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        adminService.displayWorkstations();
                        break;
                    case 2:
                        adminService.addWorkstation();
                        break;
                    case 3:
                        adminService.updateWorkstation();
                        break;
                    case 4:
                        adminService.deleteWorkstation();
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

    public void showComputerRoomMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "QUẢN LÝ PHÒNG MÁY");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Xem danh sách phòng máy");
            System.out.printf("| %-36s |\n", "2. Thêm phòng máy mới");
            System.out.printf("| %-36s |\n", "3. Cập nhật phòng máy");
            System.out.printf("| %-36s |\n", "4. Xóa phòng máy");
            System.out.printf("| %-36s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        adminService.displayAreas();
                        break;
                    case 2:
                        adminService.addArea();
                        break;
                    case 3:
                        adminService.updateArea();
                        break;
                    case 4:
                        adminService.deleteArea();
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

    public void showFoodMenuManagement() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "QUẢN LÝ MENU ĐỒ ĂN");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Xem menu đồ ăn/thức uống");
            System.out.printf("| %-36s |\n", "2. Thêm vào menu");
            System.out.printf("| %-36s |\n", "3. Cập nhật menu");
            System.out.printf("| %-36s |\n", "4. Xóa món trong menu");
            System.out.printf("| %-36s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        adminService.displayServices();
                        break;
                    case 2:
                        adminService.addService();
                        break;
                    case 3:
                        adminService.updateService();
                        break;
                    case 4:
                        adminService.deleteService();
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

    public void showUserManagementMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-36s |\n", "QUẢN LÝ NGƯỜI DÙNG");
            System.out.println("+======================================+");
            System.out.printf("| %-36s |\n", "1. Xem danh sách người dùng");
            System.out.printf("| %-36s |\n", "2. Thay đổi vai trò người dùng");
            System.out.printf("| %-36s |\n", "3. Cấm tài khoản");
            System.out.printf("| %-36s |\n", "4. Mở khoá tài khoản");
            System.out.printf("| %-36s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        adminService.displayUsers();
                        break;
                    case 2:
                        adminService.changeUserRole();
                        break;
                    case 3:
                        adminService.lockUserAccount();
                        break;
                    case 4:
                        adminService.unlockUserAccount();
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

    public void showReportMenu() {
        System.out.println("\nNhập ngày xuất báo cáo (định dạng: dd/MM/yyyy), hoặc Enter để chọn hôm nay: ");
        String dateInput = scanner.nextLine().trim();
        adminService.exportDailyRevenueToExcel(dateInput);
    }
}