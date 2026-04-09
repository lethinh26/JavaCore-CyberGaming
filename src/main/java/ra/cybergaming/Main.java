package ra.cybergaming;

import ra.cybergaming.presentation.AuthMenu;

import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        try {
            System.out.println("===== CYBER GAMING SYSTEM =====");
            AuthMenu authMenu = new AuthMenu(sc);
            authMenu.showMenu();
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }
}

