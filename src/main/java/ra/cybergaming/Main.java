package ra.cybergaming;

import ra.cybergaming.model.enums.RoleType;
import ra.cybergaming.presentation.AdminMenu;
import ra.cybergaming.presentation.AuthMenu;
import ra.cybergaming.presentation.CustomerMenu;
import ra.cybergaming.presentation.StaffMenu;
import ra.cybergaming.service.auth.SessionManager;
import ra.cybergaming.util.DBConnector;

import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        if (!DBConnector.checkDB()) return;

        try {
            System.out.println("========================================");
            System.out.println("   CYBER GAMING - HỆ THỐNG QUẢN LÝ   ");
            System.out.println("========================================\n");

            boolean isRunning = true;
            
            while (isRunning) {
                if (!SessionManager.isLoggedIn()) {
                    AuthMenu authMenu = new AuthMenu(sc);
                    authMenu.showMenu();
                } else {
                    isRunning = routeToMenu();
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

    private static boolean routeToMenu() {
        RoleType role = SessionManager.getUserRole();
        
        switch (role) {
            case ADMIN:
                AdminMenu adminMenu = new AdminMenu(sc);
                adminMenu.showMainMenu();
                break;
            case STAFF:
                StaffMenu staffMenu = new StaffMenu(sc);
                staffMenu.showMenu();
                break;
            case CUSTOMER:
                CustomerMenu customerMenu = new CustomerMenu(sc);
                customerMenu.showMenu();
                break;
            default:
                System.out.println("Lỗi: Role không hợp lệ!");
                SessionManager.logout();
        }
        
        return SessionManager.isLoggedIn();
    }
}

