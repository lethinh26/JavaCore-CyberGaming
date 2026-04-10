package ra.cybergaming.service.customer;

import ra.cybergaming.dao.impl.BookingDAO;
import ra.cybergaming.dao.impl.OrderDAO;
import ra.cybergaming.dao.impl.ServiceDAO;
import ra.cybergaming.dao.impl.WorkstationDAO;
import ra.cybergaming.model.Booking;
import ra.cybergaming.model.Order;
import ra.cybergaming.model.Service;
import ra.cybergaming.model.User;
import ra.cybergaming.model.Workstation;
import ra.cybergaming.model.enums.BookingStatus;
import ra.cybergaming.model.enums.OrderStatus;
import ra.cybergaming.service.auth.SessionManager;
import ra.cybergaming.util.InputHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CustomerService {
    private static CustomerService instance;
    private static final WorkstationDAO workstationDAO = new WorkstationDAO();
    private static final BookingDAO bookingDAO = new BookingDAO();
    private static final ServiceDAO serviceDAO = new ServiceDAO();
    private static final OrderDAO orderDAO = new OrderDAO();

    private CustomerService() {}

    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    public void showInfo() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            SessionManager.logout();
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        System.out.println("\n======================================================================");
        System.out.println("|                       THÔNG TIN TÀI KHOẢN                         |");
        System.out.println("======================================================================");
        System.out.printf("| %-20s : %-51d |%n", "ID người dùng", currentUser.getUserId());
        System.out.printf("| %-20s : %-51s |%n", "Tên tài khoản", currentUser.getUsername());
        System.out.printf("| %-20s : %-51s |%n", "Họ và tên", currentUser.getFullName());
        System.out.printf("| %-20s : %-51s |%n", "Email", currentUser.getEmail());
        System.out.printf("| %-20s : %-51s |%n", "Số điện thoại", currentUser.getPhone());
        System.out.printf("| %-20s : %-51.2f |%n", "Số dư tài khoản", currentUser.getBalance());
        System.out.printf("| %-20s : %-51s |%n", "Vai trò", currentUser.getRoleType().getDisplayName());
        System.out.printf("| %-20s : %-51s |%n", "Trạng thái", currentUser.getStatus().toString());

        if (currentUser.getCreatedAt() != null) {
            System.out.printf("| %-20s : %-51s |%n", "Ngày tạo", currentUser.getCreatedAt().format(formatter));
        }

        if (currentUser.getUpdatedAt() != null) {
            System.out.printf("| %-20s : %-51s |%n", "Cập nhật lần cuối", currentUser.getUpdatedAt().format(formatter));
        }

        System.out.println("======================================================================\n");
    }

    public void bookWorkstation() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Workstation> workstations = workstationDAO.findAll();

        if (workstations == null || workstations.isEmpty()) {
            System.out.println("Lỗi: Không có máy trạm nào trong hệ thống");
            return;
        }

        System.out.println("\n======================================================================");
        System.out.println("|                  DANH SÁCH MÁY TRẠM CÓ THỂ ĐẶT TRƯỚC               |");
        System.out.println("======================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s | %-15s |%n", "STT", "Mã máy", "Tên máy", "Giá tiền/giờ", "Thông số");
        System.out.println("----------------------------------------------------------------------");

        for (int i = 0; i < workstations.size(); i++) {
            Workstation ws = workstations.get(i);
            System.out.printf("| %-5d | %-15s | %-20s | %-15.2f | %-15s |%n",
                i + 1, ws.getStationCode(), ws.getStationName(), ws.getHourlyRate(),
                ws.getSpecification().length() > 15 ? ws.getSpecification().substring(0, 12) + "..." : ws.getSpecification());
        }

        System.out.println("======================================================================\n");

        int choice = InputHandler.inputInt("Chọn số máy muốn đặt (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy đặt máy.");
            return;
        }

        if (choice < 1 || choice > workstations.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Workstation selectedWorkstation = workstations.get(choice - 1);

        LocalDateTime startTime = null;
        while (startTime == null) {
            String dateInput = InputHandler.inputString("Nhập ngày đặt (dd/MM/yyyy HH:mm): ");
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                startTime = LocalDateTime.parse(dateInput, inputFormatter);

                if (startTime.isBefore(LocalDateTime.now())) {
                    System.out.println("Lỗi: Thời gian bắt đầu phải là tương lai");
                    startTime = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Lỗi: Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy HH:mm");
            }
        }

        Booking booking = new Booking();
        booking.setCustomerId(currentUser.getUserId());
        booking.setWorkstationId(selectedWorkstation.getWorkstationId());
        booking.setStartTime();
        booking.setBookingStatus(BookingStatus.PENDING);

        if (bookingDAO.create(booking)) {
            System.out.println("Đặt máy thành công!");
            System.out.println("Mã đặt: " + booking.getBookingCode());
            System.out.println("Máy: " + selectedWorkstation.getStationName());
            System.out.println("Thời gian: " + startTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
    }

    public void orderFood() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Booking> activeBookings = bookingDAO.findByCustomerIdAndStatus(currentUser.getUserId(), BookingStatus.ACTIVE);

        if (activeBookings == null || activeBookings.isEmpty()) {
            System.out.println("Lỗi: Bạn không có đặt máy nào đang hoạt động để đặt đồ ăn.");
            return;
        }

        System.out.println("\n======================================================================");
        System.out.println("|                       ĐẶT ĐỒ ĂN/ĐỒ UỐNG                            |");
        System.out.println("======================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s |%n", "STT", "Mã đặt máy", "Máy", "Trạng thái");
        System.out.println("----------------------------------------------------------------------");

        for (int i = 0; i < activeBookings.size(); i++) {
            Booking booking = activeBookings.get(i);
            Workstation ws = workstationDAO.findById(booking.getWorkstationId());
            System.out.printf("| %-5d | %-15s | %-20s | %-15s |%n",
                i + 1, booking.getBookingCode(),
                ws != null ? ws.getStationName() : "N/A",
                booking.getBookingStatus().toString());
        }
        System.out.println("======================================================================\n");

        int bookingChoice = InputHandler.inputInt("Chọn đặt máy để đặt đồ (0 để hủy): ");

        if (bookingChoice == 0) {
            System.out.println("Đã hủy đặt đồ.");
            return;
        }

        if (bookingChoice < 1 || bookingChoice > activeBookings.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Booking selectedBooking = activeBookings.get(bookingChoice - 1);

        Order order = new Order();
        order.setBookingId(selectedBooking.getBookingId());
        order.setCustomerId(currentUser.getUserId());
        order.setNote("");
        order.setStaffId(0);
        order.setOrderStatus(OrderStatus.PENDING);

        if (!orderDAO.create(order)) {
            System.out.println("Lỗi: Không thể tạo đơn hàng");
            return;
        }

        boolean continueOrdering = true;
        while (continueOrdering) {
            System.out.println("\n======================================================================");
            System.out.println("|                       MENU DỊCH VỤ                                |");
            System.out.println("======================================================================");
            System.out.println("1. Đồ ăn");
            System.out.println("2. Đồ uống");
            System.out.println("0. Thoát đặt đồ");
            System.out.println("======================================================================\n");

            int menuChoice = InputHandler.inputInt("Chọn loại dịch vụ: ");

            if (menuChoice == 0) {
                continueOrdering = false;
                continue;
            }

            List<Service> services;
            if (menuChoice == 1) {
                services = serviceDAO.findAll();
            } else if (menuChoice == 2) {
                services = serviceDAO.findAll();
            } else {
                System.out.println("Lỗi: Lựa chọn không hợp lệ");
                continue;
            }

            if (services == null || services.isEmpty()) {
                System.out.println("Không có dịch vụ nào available.");
                continue;
            }

            System.out.println("\n----------------------------------------------------------------------");
            System.out.printf("| %-5s | %-20s | %-15s | %-10s | %-10s |%n", "STT", "Tên dịch vụ", "Giá", "Tồn kho", "Loại");
            System.out.println("----------------------------------------------------------------------");

            for (int i = 0; i < services.size(); i++) {
                Service service = services.get(i);
                System.out.printf("| %-5d | %-20s | %-15.2f | %-10d | %-10s |%n",
                    i + 1, service.getServiceName(), service.getPrice(),
                    service.getStock_quantity(), service.getCategory().toString());
            }
            System.out.println("----------------------------------------------------------------------\n");

            int serviceChoice = InputHandler.inputInt("Chọn dịch vụ (0 để quay lại): ");

            if (serviceChoice == 0) {
                continue;
            }

            if (serviceChoice < 1 || serviceChoice > services.size()) {
                System.out.println("Lỗi: Lựa chọn không hợp lệ");
                continue;
            }

            Service selectedService = services.get(serviceChoice - 1);

            int quantity = InputHandler.inputInt("Nhập số lượng: ");

            if (quantity <= 0) {
                System.out.println("Lỗi: Số lượng phải lớn hơn 0");
                continue;
            }

            if (quantity > selectedService.getStock_quantity()) {
                System.out.println("Lỗi: Số lượng vượt quá tồn kho. Tồn kho hiện tại: " + selectedService.getStock_quantity());
                continue;
            }

            orderDAO.updateOrderTotal(order.getOrderId());

            System.out.println("\nĐã thêm vào đơn: " + selectedService.getServiceName() + " x" + quantity);
            System.out.println("Đơn hàng hiện tại: " + order.getOrderCode());
        }

        orderDAO.updateOrderTotal(order.getOrderId());
        Order finalOrder = orderDAO.findById(order.getOrderId());

        System.out.println("\n======================================================================");
        System.out.println("|                       THÔNG TIN ĐƠN HÀNG                          |");
        System.out.println("======================================================================");
        System.out.printf("| Mã đơn hàng: %-55s |%n", finalOrder.getOrderCode());
        System.out.printf("| Tổng tiền  : %-55.2f |%n", finalOrder.getTotalAmount());
        System.out.println("======================================================================\n");
    }

    public void viewTransactionHistory() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("\n======================================================================");
        System.out.println("|                  LỊCH SỬ GIAO DỊCH CỦA BẠN                         |");
        System.out.println("======================================================================");
        System.out.printf("| Tên tài khoản: %-60s |%n", currentUser.getUsername());
        System.out.printf("| Họ và tên    : %-60s |%n", currentUser.getFullName());
        System.out.println("======================================================================\n");

        List<Booking> bookings = bookingDAO.findByCustomerId(currentUser.getUserId());

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("| Không có dữ liệu đặt máy                                          |");
        } else {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("|                         ĐẶT MÁY                                    |");
            System.out.println("----------------------------------------------------------------------");
            System.out.printf("| %-15s | %-15s | %-20s | %-15s | %-10s |%n",
                "Mã đặt", "Máy trạm", "Thời gian bắt đầu", "Tổng tiền", "Trạng thái");
            System.out.println("----------------------------------------------------------------------");

            for (Booking booking : bookings) {
                Workstation ws = workstationDAO.findById(booking.getWorkstationId());
                String workstationName = ws != null ? ws.getStationName() : "N/A";
                String startTimeStr = booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A";

                System.out.printf("| %-15s | %-15s | %-20s | %-15.2f | %-10s |%n",
                    booking.getBookingCode(),
                    workstationName.length() > 15 ? workstationName.substring(0, 12) + "..." : workstationName,
                    startTimeStr,
                    booking.getTotalAmount(),
                    booking.getBookingStatus().toString());
            }
            System.out.println("----------------------------------------------------------------------\n");
        }

        List<Order> orders = orderDAO.findByCustomerId(currentUser.getUserId());

        if (orders == null || orders.isEmpty()) {
            System.out.println("| Không có dữ liệu đơn hàng                                         |");
        } else {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("|                         ĐƠN HÀNG                                  |");
            System.out.println("----------------------------------------------------------------------");
            System.out.printf("| %-15s | %-15s | %-15s | %-15s |%n",
                "Mã đơn", "Đặt máy", "Tổng tiền", "Trạng thái");
            System.out.println("----------------------------------------------------------------------");

            for (Order order : orders) {
                String bookingCode = "N/A";
                List<Booking> bookingList = bookingDAO.findByCustomerId(currentUser.getUserId());
                for (Booking b : bookingList) {
                    if (b.getBookingId() == order.getBookingId()) {
                        bookingCode = b.getBookingCode();
                        break;
                    }
                }

                System.out.printf("| %-15s | %-15s | %-15.2f | %-15s |%n",
                    order.getOrderCode(),
                    bookingCode,
                    order.getTotalAmount(),
                    order.getOrderStatus().toString());
            }
            System.out.println("----------------------------------------------------------------------\n");
        }

        System.out.println("======================================================================\n");
    }

    public void cancelBooking() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Booking> pendingBookings = bookingDAO.findByCustomerIdAndStatus(currentUser.getUserId(), BookingStatus.PENDING);

        if (pendingBookings == null || pendingBookings.isEmpty()) {
            System.out.println("Bạn không có đặt máy nào đang chờ để hủy.");
            return;
        }

        System.out.println("\n======================================================================");
        System.out.println("|                  DANH SÁCH ĐẶT MÁY CHỜ HỦY                         |");
        System.out.println("======================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-20s |%n", "STT", "Mã đặt", "Máy", "Thời gian bắt đầu");
        System.out.println("----------------------------------------------------------------------");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int i = 0; i < pendingBookings.size(); i++) {
            Booking booking = pendingBookings.get(i);
            Workstation ws = workstationDAO.findById(booking.getWorkstationId());
            String startTimeStr = booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A";
            System.out.printf("| %-5d | %-15s | %-20s | %-20s |%n",
                i + 1, booking.getBookingCode(),
                ws != null ? ws.getStationName() : "N/A",
                startTimeStr);
        }
        System.out.println("======================================================================\n");

        int choice = InputHandler.inputInt("Chọn đặt máy cần hủy (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > pendingBookings.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Booking selectedBooking = pendingBookings.get(choice - 1);
        if (bookingDAO.cancelBooking(selectedBooking.getBookingId())) {
            System.out.println("Hủy đặt máy thành công!");
        }
    }

    public void viewPendingBookings() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Booking> pendingBookings = bookingDAO.findByCustomerIdAndStatus(currentUser.getUserId(), BookingStatus.PENDING);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("\n======================================================================");
        System.out.println("|                  DANH SÁCH ĐẶT MÁY ĐANG CHỜ                        |");
        System.out.println("======================================================================");

        if (pendingBookings == null || pendingBookings.isEmpty()) {
            System.out.println("| Không có đặt máy nào đang chờ                                     |");
        } else {
            System.out.printf("| %-15s | %-20s | %-20s | %-15s |%n", "Mã đặt", "Máy", "Thời gian bắt đầu", "Trạng thái");
            System.out.println("----------------------------------------------------------------------");
            for (Booking booking : pendingBookings) {
                Workstation ws = workstationDAO.findById(booking.getWorkstationId());
                String startTimeStr = booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A";
                System.out.printf("| %-15s | %-20s | %-20s | %-15s |%n",
                    booking.getBookingCode(),
                    ws != null ? ws.getStationName() : "N/A",
                    startTimeStr,
                    booking.getBookingStatus().toString());
            }
        }
        System.out.println("======================================================================\n");
    }

    public void viewCancelledBookings() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Booking> cancelledBookings = bookingDAO.findByCustomerIdAndStatus(currentUser.getUserId(), BookingStatus.CANCELLED);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("\n======================================================================");
        System.out.println("|                  DANH SÁCH ĐẶT MÁY ĐÃ HỦY                         |");
        System.out.println("======================================================================");

        if (cancelledBookings == null || cancelledBookings.isEmpty()) {
            System.out.println("| Không có đặt máy nào đã hủy                                        |");
        } else {
            System.out.printf("| %-15s | %-20s | %-20s | %-15s |%n", "Mã đặt", "Máy", "Thời gian bắt đầu", "Trạng thái");
            System.out.println("----------------------------------------------------------------------");
            for (Booking booking : cancelledBookings) {
                Workstation ws = workstationDAO.findById(booking.getWorkstationId());
                String startTimeStr = booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A";
                System.out.printf("| %-15s | %-20s | %-20s | %-15s |%n",
                    booking.getBookingCode(),
                    ws != null ? ws.getStationName() : "N/A",
                    startTimeStr,
                    booking.getBookingStatus().toString());
            }
        }
        System.out.println("======================================================================\n");
    }

    public void cancelOrder() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Order> pendingOrders = orderDAO.findByCustomerId(currentUser.getUserId()).stream()
            .filter(o -> o.getOrderStatus() == OrderStatus.PENDING)
            .toList();

        if (pendingOrders == null || pendingOrders.isEmpty()) {
            System.out.println("Bạn không có đơn hàng nào đang chờ để hủy.");
            return;
        }

        System.out.println("\n======================================================================");
        System.out.println("|                  DANH SÁCH ĐƠN HÀNG CHỜ HỦY                        |");
        System.out.println("======================================================================");
        System.out.printf("| %-5s | %-15s | %-15s | %-15s |%n", "STT", "Mã đơn", "Đặt máy", "Tổng tiền");
        System.out.println("----------------------------------------------------------------------");

        for (int i = 0; i < pendingOrders.size(); i++) {
            Order order = pendingOrders.get(i);
            String bookingCode = "N/A";
            List<Booking> bookings = bookingDAO.findByCustomerId(currentUser.getUserId());
            for (Booking b : bookings) {
                if (b.getBookingId() == order.getBookingId()) {
                    bookingCode = b.getBookingCode();
                    break;
                }
            }
            System.out.printf("| %-5d | %-15s | %-15s | %-15.2f |%n",
                i + 1, order.getOrderCode(), bookingCode, order.getTotalAmount());
        }
        System.out.println("======================================================================\n");

        int choice = InputHandler.inputInt("Chọn đơn hàng cần hủy (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > pendingOrders.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Order selectedOrder = pendingOrders.get(choice - 1);
        if (orderDAO.delete(selectedOrder.getOrderId())) {
            System.out.println("Hủy đơn hàng thành công!");
        }
    }

    public void viewPendingOrders() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Order> pendingOrders = orderDAO.findByCustomerId(currentUser.getUserId()).stream()
            .filter(o -> o.getOrderStatus() == OrderStatus.PENDING)
            .toList();

        System.out.println("\n======================================================================");
        System.out.println("|                  DANH SÁCH ĐƠN HÀNG ĐANG CHỜ                      |");
        System.out.println("======================================================================");

        if (pendingOrders == null || pendingOrders.isEmpty()) {
            System.out.println("| Không có đơn hàng nào đang chờ                                     |");
        } else {
            System.out.printf("| %-15s | %-15s | %-15s | %-15s |%n", "Mã đơn", "Đặt máy", "Tổng tiền", "Trạng thái");
            System.out.println("----------------------------------------------------------------------");
            for (Order order : pendingOrders) {
                String bookingCode = "N/A";
                List<Booking> bookings = bookingDAO.findByCustomerId(currentUser.getUserId());
                for (Booking b : bookings) {
                    if (b.getBookingId() == order.getBookingId()) {
                        bookingCode = b.getBookingCode();
                        break;
                    }
                }
                System.out.printf("| %-15s | %-15s | %-15.2f | %-15s |%n",
                    order.getOrderCode(), bookingCode, order.getTotalAmount(), order.getOrderStatus().toString());
            }
        }
        System.out.println("======================================================================\n");
    }

    public void viewCancelledOrders() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Order> cancelledOrders = orderDAO.findByCustomerId(currentUser.getUserId()).stream()
            .filter(o -> o.getOrderStatus() == OrderStatus.CANCELLED)
            .toList();

        System.out.println("\n======================================================================");
        System.out.println("|                  DANH SÁCH ĐƠN HÀNG ĐÃ HỦY                        |");
        System.out.println("======================================================================");

        if (cancelledOrders == null || cancelledOrders.isEmpty()) {
            System.out.println("| Không có đơn hàng nào đã hủy                                       |");
        } else {
            System.out.printf("| %-15s | %-15s | %-15s | %-15s |%n", "Mã đơn", "Đặt máy", "Tổng tiền", "Trạng thái");
            System.out.println("----------------------------------------------------------------------");
            for (Order order : cancelledOrders) {
                String bookingCode = "N/A";
                List<Booking> bookings = bookingDAO.findByCustomerId(currentUser.getUserId());
                for (Booking b : bookings) {
                    if (b.getBookingId() == order.getBookingId()) {
                        bookingCode = b.getBookingCode();
                        break;
                    }
                }
                System.out.printf("| %-15s | %-15s | %-15.2f | %-15s |%n",
                    order.getOrderCode(), bookingCode, order.getTotalAmount(), order.getOrderStatus().toString());
            }
        }
        System.out.println("======================================================================\n");
    }
}
