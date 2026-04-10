package ra.cybergaming.presentation;

import java.util.Scanner;

public class AdminMenu {
    private Scanner scanner;

    public AdminMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMainMenu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n+======================================+");
            System.out.printf("| %-38s |\n", "MENU QUẢN LÝ - ADMIN");
            System.out.println("+======================================+");
            System.out.printf("| %-38s |\n", "1. Quản lý danh sách máy trạm");
            System.out.printf("| %-38s |\n", "2. Quản lý phòng máy");
            System.out.printf("| %-38s |\n", "3. Quản lý menu đồ ăn, thức uống");
            System.out.printf("| %-38s |\n", "4. Xem báo cáo tổng quát");
            System.out.printf("| %-38s |\n", "0. Đăng xuất");
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
                        System.out.println("Bạn chọn: Xem báo cáo tổng quát");
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
                        System.out.println("Bạn chọn: Xem danh sách máy trạm");
                        break;
                    case 2:
                        System.out.println("Bạn chọn: Thêm máy trạm mới");
                        break;
                    case 3:
                        System.out.println("Bạn chọn: Cập nhật máy trạm");
                        break;
                    case 4:
                        System.out.println("Bạn chọn: Xóa máy trạm");
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
            System.out.printf("| %-38s |\n", "QUẢN LÝ PHÒNG MÁY");
            System.out.println("+======================================+");
            System.out.printf("| %-38s |\n", "1. Xem danh sách phòng máy");
            System.out.printf("| %-38s |\n", "2. Thêm phòng máy mới");
            System.out.printf("| %-38s |\n", "3. Cập nhật phòng máy");
            System.out.printf("| %-38s |\n", "4. Xóa phòng máy");
            System.out.printf("| %-38s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        System.out.println("Bạn chọn: Xem danh sách phòng máy");
                        break;
                    case 2:
                        System.out.println("Bạn chọn: Thêm phòng máy mới");
                        break;
                    case 3:
                        System.out.println("Bạn chọn: Cập nhật phòng máy");
                        break;
                    case 4:
                        System.out.println("Bạn chọn: Xóa phòng máy");
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
            System.out.printf("| %-38s |\n", "QUẢN LÝ MENU ĐỒ ĂN");
            System.out.println("+======================================+");
            System.out.printf("| %-38s |\n", "1. Xem menu đồ ăn/thức uống");
            System.out.printf("| %-38s |\n", "2. Thêm vào menu");
            System.out.printf("| %-38s |\n", "3. Cập nhật menu");
            System.out.printf("| %-38s |\n", "4. Xóa món trong menu");
            System.out.printf("| %-38s |\n", "0. Quay lại");
            System.out.println("+======================================+");
            System.out.print("Chọn: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        System.out.println("Bạn chọn: Xem menu đồ ăn/thức uống");
                        break;
                    case 2:
                        System.out.println("Bạn chọn: Thêm vào menu");
                        break;
                    case 3:
                        System.out.println("Bạn chọn: Cập nhật menu");
                        break;
                    case 4:
                        System.out.println("Bạn chọn: Xóa món trong menu");
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