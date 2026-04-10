package ra.cybergaming.util;


import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class InputHandler {
    private static Scanner sc = new Scanner(System.in);

    public static String inputString(String text) {
        while (true) {
            System.out.print(text);
            String newStr = sc.nextLine().trim();

            if (newStr.isEmpty()) {
                System.out.println("Lỗi: Không được nhập rỗng. Vui lòng nhập lại.");
                continue;
            }

            return newStr;
        }
    }

    public static int inputInt(String text) {
        while (true) {
            System.out.print(text);
            try {
                int newInt = Integer.parseInt(sc.nextLine().trim());

                if (newInt < 0) {
                    System.out.println("Lỗi: Số phải lớn hơn hoặc bằng 0. Vui lòng nhập lại.");
                    continue;
                }
                return newInt;
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Nhập vào phải là số. Vui lòng nhập lại.");
            }
        }
    }

    public static double inputDouble(String text) {
        while (true) {
            System.out.print(text);
            try {
                double newDouble = Double.parseDouble(sc.nextLine().trim());

                if (newDouble < 0) {
                    System.out.println("Lỗi: Số phải lớn hơn hoặc bằng 0. Vui lòng nhập lại.");
                    continue;
                }
                return newDouble;
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Nhập vào phải là số. Vui lòng nhập lại.");
            }
        }
    }

    public static <T extends Enum<T>> T inputStatus(String text, Class<T> enumClass) {
        while (true) {
            System.out.print(text);
            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Lỗi: Không được để trống. Vui lòng nhập lại.");
                continue;
            }

            try {
                return Enum.valueOf(enumClass, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                String values = Arrays.stream(enumClass.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                String textError = "Lỗi: Trạng thái không hợp lệ. Các giá trị hợp lệ:  " + values + ". Vui lòng nhập lại.";
                System.out.println(textError);
            }
        }
    }

    public static String inputEmail(String text) {
        while (true) {
            System.out.print(text);
            String email = sc.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("Lỗi: Không được để trống. Vui lòng nhập lại.");
                continue;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("Lỗi: Email không hợp lệ. Vui lòng nhập lại.");
                continue;
            }

            return email;
        }
    }

    public static String inputPassword(String text) {
        while (true) {
            System.out.print(text);
            String pass = sc.nextLine().trim();

            if (pass.isEmpty()) {
                System.out.println("Lỗi: Không được để trống. Vui lòng nhập lại.");
                continue;
            }

            if (pass.length() < 6) {
                System.out.println("Lỗi: Độ dài mật khẩu phải lớn hơn 6 kí tự. Vui lòng nhập lại.");
                continue;
            }

            return pass;
        }
    }

    public static String inputUsername(String text) {
        while (true) {
            System.out.print(text);
            String username = sc.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("Lỗi: Không được để trống. Vui lòng nhập lại.");
                continue;
            }

            if (username.length() < 4) {
                System.out.println("Lỗi: Tên tài khoản phải lớn hơn 4 kí tự. Vui lòng nhập lại.");
                continue;
            }

            if (username.contains(" ")) {
                System.out.println("Lỗi: Tên tài khoản không được chứa dấu cách. Vui lòng nhập lại.");
                continue;
            }

            return username;
        }
    }

    public static String inputPhone(String text) {
        while (true) {
            System.out.print(text);
            String phone = sc.nextLine().trim();

            if (phone.isEmpty()) {
                System.out.println("Lỗi: Không được để trống. Vui lòng nhập lại.");
                continue;
            }

            if (!phone.matches("^0[0-9]{9}$")) {
                System.out.println("Lỗi: Số điện thoại không hợp lệ. Vui lòng nhập lại");
                continue;
            }

            return phone;
        }
    }


}
