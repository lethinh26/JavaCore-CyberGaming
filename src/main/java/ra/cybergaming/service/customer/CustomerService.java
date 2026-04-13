package ra.cybergaming.service.customer;

import ra.cybergaming.dao.impl.*;
import ra.cybergaming.model.Booking;
import ra.cybergaming.model.Order;
import ra.cybergaming.model.OrderItem;
import ra.cybergaming.model.Service;
import ra.cybergaming.model.User;
import ra.cybergaming.model.Workstation;
import ra.cybergaming.model.enums.BookingStatus;
import ra.cybergaming.model.enums.CategoryType;
import ra.cybergaming.model.enums.OrderStatus;
import ra.cybergaming.model.enums.PaymentStatus;
import ra.cybergaming.service.auth.SessionManager;
import ra.cybergaming.util.InputHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    private WorkstationDAO workstationDAO;
    private BookingDAO bookingDAO;
    private ServiceDAO serviceDAO;
    private OrderDAO orderDAO;
    private UserDAO userDAO;

    protected final List<OrderItem> orderItemList = new ArrayList<>();
    private Order currentOrder = null;

    public CustomerService() {
        this.workstationDAO = new WorkstationDAO();
        this.bookingDAO = new BookingDAO();
        this.serviceDAO = new ServiceDAO();
        this.orderDAO = new OrderDAO();
        this.userDAO = new UserDAO();
    }

    public void showInfo() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            SessionManager.logout();
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        System.out.println("\n=====================================================================");
        System.out.println("|                       THÔNG TIN TÀI KHOẢN                         |");
        System.out.println("=====================================================================");
        System.out.printf("| %-20s : %-42d |%n", "ID người dùng", currentUser.getUserId());
        System.out.printf("| %-20s : %-42s |%n", "Tên tài khoản", currentUser.getUsername());
        System.out.printf("| %-20s : %-42s |%n", "Họ và tên", currentUser.getFullName());
        System.out.printf("| %-20s : %-42s |%n", "Email", currentUser.getEmail());
        System.out.printf("| %-20s : %-42s |%n", "Số điện thoại", currentUser.getPhone());
        System.out.printf("| %-20s : %-42.2f |%n", "Số dư tài khoản", currentUser.getBalance());
        System.out.printf("| %-20s : %-42s |%n", "Vai trò", currentUser.getRoleType().getDisplayName());
        System.out.printf("| %-20s : %-42s |%n", "Trạng thái", currentUser.getStatus().toString());

        if (currentUser.getCreatedAt() != null) {
            System.out.printf("| %-20s : %-42s |%n", "Ngày tạo", currentUser.getCreatedAt().format(formatter));
        }

        if (currentUser.getUpdatedAt() != null) {
            System.out.printf("| %-20s : %-42s |%n", "Cập nhật lần cuối", currentUser.getUpdatedAt().format(formatter));
        }

        System.out.println("=====================================================================\n");
    }

    public void bookWorkstation() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Workstation> workstations = workstationDAO.findAllAvailable();

        if (workstations == null || workstations.isEmpty()) {
            System.out.println("Lỗi: Không có máy trạm nào trong hệ thống");
            return;
        }

        System.out.println("\n======================================================================================");
        System.out.printf("| %-82s |%n", "DANH SÁCH MÁY TRẠM CÓ THỂ ĐẶT TRƯỚC");
        System.out.println("======================================================================================");
        System.out.printf("| %-4s | %-10s | %-15s | %-15s | %-26s |%n", "STT", "Mã máy", "Tên máy", "Giá tiền/h", "Thông số");
        System.out.println("--------------------------------------------------------------------------------------");

        for (int i = 0; i < workstations.size(); i++) {
            Workstation ws = workstations.get(i);
            System.out.printf("| %-4d | %-10s | %-15s | %-15.2f | %-26s |%n",
                i + 1, ws.getStationCode(), ws.getStationName(), ws.getHourlyRate(),
                ws.getSpecification().length() > 26 ? ws.getSpecification().substring(0, 12) + "..." : ws.getSpecification());
        }

        System.out.println("======================================================================================\n");

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
        booking.setStartTime(startTime);
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
            System.out.println("Lỗi: Bạn không có máy nào đang hoạt động để đặt đồ ăn.");
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

        int bookingChoice = InputHandler.inputInt("Chọn máy để đặt đồ (0 để hủy): ");

        if (bookingChoice == 0) {
            System.out.println("Đã hủy đặt đồ.");
            return;
        }

        if (bookingChoice < 1 || bookingChoice > activeBookings.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Booking selectedBooking = activeBookings.get(bookingChoice - 1);

        currentOrder = new Order();
        currentOrder.setBookingId(selectedBooking.getBookingId());
        currentOrder.setCustomerId(currentUser.getUserId());
        currentOrder.setNote("");
        currentOrder.setOrderStatus(OrderStatus.PENDING);

        orderItemList.clear();

        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n======================================================================");
            System.out.println("|                       MENU DỊCH VỤ                                |");
            System.out.println("======================================================================");
            System.out.println("| 1. Đồ ăn                                                         |");
            System.out.println("| 2. Đồ uống                                                       |");
            System.out.println("| 3. Xem đơn hàng                                                  |");
            System.out.println("| 4. Xoá món trong đơn hàng                                        |");
            System.out.println("| 5. Đặt hàng                                                      |");
            System.out.println("| 0. Thoát                                                         |");
            System.out.println("======================================================================\n");

            int menuChoice = InputHandler.inputInt("Chọn: ");

            switch (menuChoice) {
                case 1:
                    orderFoodCategory(1);
                    break;
                case 2:
                    orderFoodCategory(2);
                    break;
                case 3:
                    viewCurrentOrder();
                    break;
                case 4:
                    deleteOrderItem();
                    break;
                case 5:
                    placeOrder();
                    isRunning = false;
                    break;
                case 0:
                    orderItemList.clear();
                    isRunning = false;
                    break;
                default:
                    System.out.println("Lỗi: Lựa chọn không hợp lệ");
            }
        }
    }

    public void orderFoodCategory(int category) {
        CategoryType categoryType = (category == 1) ? CategoryType.FOOD : CategoryType.DRINK;
        List<Service> services = serviceDAO.findByCategory(categoryType);

        if (services == null || services.isEmpty()) {
            System.out.println("Không có dịch vụ nào.");
            return;
        }

        String categoryName = (category == 1) ? "ĐỒ ĂN" : "ĐỒ UỐNG";
        System.out.println("\n====================================================================");
        System.out.printf("| %-62s |%n", "DANH SÁCH DỊCH VỤ");
        System.out.println("====================================================================");
        System.out.printf("| %-5s | %-20s | %-15s | %-10s | %-10s |%n", "STT", "Tên dịch vụ", "Giá", "Tồn kho", categoryName);
        System.out.println("====================================================================");

        for (int i = 0; i < services.size(); i++) {
            Service service = services.get(i);
            System.out.printf("| %-5d | %-20s | %-15.2f | %-10d | %-10s |%n",
                i + 1, service.getServiceName(), service.getPrice(),
                service.getStock_quantity(), service.getCategory().toString());
        }
        System.out.println("====================================================================\n");

        int serviceChoice = InputHandler.inputInt("Chọn món (0 để quay lại): ");

        if (serviceChoice == 0) {
            return;
        }

        if (serviceChoice < 1 || serviceChoice > services.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Service selectedService = services.get(serviceChoice - 1);

        int quantity = InputHandler.inputInt("Nhập số lượng: ");

        if (quantity <= 0) {
            System.out.println("Lỗi: Số lượng phải lớn hơn 0");
            return;
        }

        if (quantity > selectedService.getStock_quantity()) {
            System.out.println("Lỗi: Số lượng vượt quá tồn kho. Tồn kho hiện tại: " + selectedService.getStock_quantity());
            return;
        }

        // Create OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setServiceId(selectedService.getServiceId());
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(selectedService.getPrice());
        orderItem.setLineTotal(quantity * selectedService.getPrice());

        orderItemList.add(orderItem);
        System.out.println("\nĐã thêm vào đơn: " + selectedService.getServiceName() + " x" + quantity);
    }

    public void viewCurrentOrder() {
        if (orderItemList.isEmpty()) {
            System.out.println("Không có món nào trong giỏ.");
            return;
        }

        System.out.println("\n====================================================================================");
        System.out.printf("| %-90s |%n", "ĐƠN HÀNG HIỆN TẠI");
        System.out.println("====================================================================================");
        System.out.printf("| %-5s | %-20s | %-15s | %-10s | %-15s |%n", "STT", "Tên dịch vụ", "Giá", "Số lượng", "Thành tiền");
        System.out.println("====================================================================================");

        double totalAmount = 0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem item = orderItemList.get(i);
            Service service = serviceDAO.findById(item.getServiceId());
            String serviceName = service != null ? service.getServiceName() : "N/A";
            System.out.printf("| %-5d | %-20s | %-15.2f | %-10d | %-15.2f |%n",
                i + 1, serviceName, item.getUnitPrice(), item.getQuantity(), item.getLineTotal());
            totalAmount += item.getLineTotal();
        }
        System.out.println("====================================================================================");
        System.out.printf("| %-5s | %-20s | %-15s | %-10s | %-15.2f |%n", "", "", "", "Tổng:", totalAmount);
        System.out.println("====================================================================================\n");
    }

    public void deleteOrderItem() {
        if (orderItemList.isEmpty()) {
            System.out.println("Không có món nào để xoá.");
            return;
        }

        System.out.println("\n====================================================================================");
        System.out.printf("| %-90s |%n", "CHỌN MÓN ĐỂ XOÁ");
        System.out.println("====================================================================================");
        System.out.printf("| %-5s | %-20s | %-15s | %-10s | %-15s |%n", "STT", "Tên dịch vụ", "Giá", "Số lượng", "Thành tiền");
        System.out.println("====================================================================================");

        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem item = orderItemList.get(i);
            Service service = serviceDAO.findById(item.getServiceId());
            String serviceName = service != null ? service.getServiceName() : "N/A";
            System.out.printf("| %-5d | %-20s | %-15.2f | %-10d | %-15.2f |%n",
                i + 1, serviceName, item.getUnitPrice(), item.getQuantity(), item.getLineTotal());
        }
        System.out.println("====================================================================================\n");

        int choice = InputHandler.inputInt("Chọn số món cần xoá (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > orderItemList.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        OrderItem removedItem = orderItemList.remove(choice - 1);
        Service removedService = serviceDAO.findById(removedItem.getServiceId());
        String removedServiceName = removedService != null ? removedService.getServiceName() : "N/A";
        System.out.println("Đã xoá: " + removedServiceName + " x" + removedItem.getQuantity());
    }

    public void placeOrder() {
        if (orderItemList.isEmpty()) {
            System.out.println("Không có món nào để đặt.");
            return;
        }

        double totalAmount = 0;
        for (OrderItem item : orderItemList) {
            totalAmount += item.getLineTotal();
        }

        System.out.println("\n====================================================================================");
        System.out.printf("| %-90s |%n", "ĐƠN HÀNG CUỐI CÙNG");
        System.out.println("====================================================================================");
        System.out.printf("| %-5s | %-20s | %-15s | %-10s | %-15s |%n", "STT", "Tên dịch vụ", "Giá", "Số lượng", "Thành tiền");
        System.out.println("====================================================================================");

        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem item = orderItemList.get(i);
            Service service = serviceDAO.findById(item.getServiceId());
            String serviceName = service != null ? service.getServiceName() : "N/A";
            System.out.printf("| %-5d | %-20s | %-15.2f | %-10d | %-15.2f |%n",
                i + 1, serviceName, item.getUnitPrice(), item.getQuantity(), item.getLineTotal());
        }
        System.out.println("====================================================================================");
        System.out.printf("| %-5s | %-20s | %-15s | %-10s | %-15.2f |%n", "", "", "", "Tổng:", totalAmount);
        System.out.println("====================================================================================\n");

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        if (currentUser.getBalance() < totalAmount) {
            System.out.printf("Tài khoản không đủ tiền!%n");
            System.out.printf("Số dư hiện tại: %.2f%n", currentUser.getBalance());
            System.out.printf("Số tiền cần: %.2f%n", totalAmount);
            System.out.printf("Thiếu: %.2f%n", totalAmount - currentUser.getBalance());
            orderItemList.clear();
            currentOrder = null;
            return;
        }

        String note = InputHandler.inputString("Nhập ghi chú: ");
        
        currentOrder.setTotalAmount(totalAmount);
        if (note != null && !note.isEmpty()) {
            currentOrder.setNote(note);
        }

        if (orderDAO.create(currentOrder)) {
            double newBalance = currentUser.getBalance() - totalAmount;
            currentUser.setBalance(newBalance);
            userDAO.update(currentUser);
            
            System.out.println("\n✓ Đặt hàng thành công!");
            System.out.println("Mã đơn hàng: " + currentOrder.getOrderCode());
            System.out.printf("Tổng tiền: %.2f%n", totalAmount);
            System.out.printf("Số dư còn lại: %.2f%n", newBalance);
            
            for (OrderItem item : orderItemList) {
                item.setOrderId(currentOrder.getOrderId());
            }
            
            orderItemList.clear();
            currentOrder = null;
        } else {
            System.out.println("Đặt hàng thất bại. Vui lòng thử lại.");
        }
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
            System.out.println("====================================================================================");
            System.out.println("|                         ĐẶT MÁY                                    |");
            System.out.println("====================================================================================");
            System.out.printf("| %-15s | %-15s | %-20s | %-15s | %-10s |%n",
                "Mã đặt", "Máy trạm", "Thời gian bắt đầu", "Tổng tiền", "Trạng thái");
            System.out.println("====================================================================================");

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
            System.out.println("====================================================================================\n");
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

        System.out.println("\n=========================================================================");
        System.out.printf("| %-69s |%n", "DANH SÁCH ĐẶT MÁY CHỜ HỦY");
        System.out.println("=========================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-20s |%n", "STT", "Mã đặt", "Máy", "Thời gian bắt đầu");
        System.out.println("-------------------------------------------------------------------------");

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
        System.out.println("=========================================================================\n");

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

        System.out.println("\n==============================================================================");
        System.out.printf("| %-82s |%n", "DANH SÁCH ĐẶT MÁY ĐANG CHỜ");
        System.out.println("==============================================================================");

        if (pendingBookings == null || pendingBookings.isEmpty()) {
            System.out.println("| Không có đặt máy nào đang chờ                                        |");
        } else {
            System.out.printf("| %-15s | %-20s | %-20s | %-15s |%n", "Mã đặt", "Máy", "Thời gian bắt đầu", "Trạng thái");
            System.out.println("==============================================================================");
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
        System.out.println("==============================================================================\n");
    }

    public void viewCancelledBookings() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Booking> cancelledBookings = bookingDAO.findByCustomerIdAndStatus(currentUser.getUserId(), BookingStatus.CANCELLED);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("\n==============================================================================");
        System.out.printf("| %-82s |%n", "DANH SÁCH ĐẶT MÁY ĐÃ HỦY");
        System.out.println("==============================================================================");

        if (cancelledBookings == null || cancelledBookings.isEmpty()) {
            System.out.println("| Không có đặt máy nào đã hủy                                            |");
        } else {
            System.out.printf("| %-15s | %-20s | %-20s | %-15s |%n", "Mã đặt", "Máy", "Thời gian bắt đầu", "Trạng thái");
            System.out.println("==============================================================================");
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
        System.out.println("==============================================================================\n");
    }

    public void closeWorkstation() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Booking> activeBookings = bookingDAO.findByCustomerIdAndStatus(currentUser.getUserId(), BookingStatus.ACTIVE);

        if (activeBookings == null || activeBookings.isEmpty()) {
            System.out.println("Bạn không có máy nào đang hoạt động để đóng.");
            return;
        }

        System.out.println("\n=========================================================================");
        System.out.printf("| %-69s |%n", "DANH SÁCH MÁY ĐANG HOẠT ĐỘNG");
        System.out.println("=========================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-20s |%n", "STT", "Mã đặt", "Máy", "Thời gian bắt đầu");
        System.out.println("=========================================================================");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int i = 0; i < activeBookings.size(); i++) {
            Booking booking = activeBookings.get(i);
            Workstation ws = workstationDAO.findById(booking.getWorkstationId());
            String startTimeStr = booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A";
            System.out.printf("| %-5d | %-15s | %-20s | %-20s |%n",
                i + 1, booking.getBookingCode(),
                ws != null ? ws.getStationName() : "N/A",
                startTimeStr);
        }
        System.out.println("=========================================================================\n");

        int choice = InputHandler.inputInt("Chọn máy cần đóng (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > activeBookings.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Booking selectedBooking = activeBookings.get(choice - 1);
        
        LocalDateTime endTime = LocalDateTime.now();
        selectedBooking.setEndTime(endTime);
        
        LocalDateTime startTime = selectedBooking.getStartTime();
        
        long durationMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        double durationHours = durationMinutes / 60.0;
        
        Workstation ws = workstationDAO.findById(selectedBooking.getWorkstationId());
        double hourlyRate = ws != null ? ws.getHourlyRate() : 0;
        
        double totalCost = durationHours * hourlyRate;
        selectedBooking.setTotalAmount(totalCost);
        
        double newBalance = currentUser.getBalance() - totalCost;
        
        if (newBalance < 0) {
            selectedBooking.setPaymentStatus(PaymentStatus.UNPAID);
            System.out.println("\nTài khoản không đủ tiền!");
            System.out.printf("Thời gian sử dụng: %.2f giờ%n", durationHours);
            System.out.printf("Giá/giờ: %.2f%n", hourlyRate);
            System.out.printf("Số tiền cần trừ: %.2f%n", totalCost);
            System.out.printf("Số dư hiện tại: %.2f%n", currentUser.getBalance());
            System.out.printf("Khoản nợ: %.2f%n", Math.abs(newBalance));
            System.out.println("Trạng thái: UNPAID");
        } else {
            selectedBooking.setPaymentStatus(PaymentStatus.PAID);
            currentUser.setBalance(newBalance);
            System.out.println("\nĐóng máy thành công!");
            System.out.printf("Thời gian sử dụng: %.2f giờ%n", durationHours);
            System.out.printf("Giá/giờ: %.2f%n", hourlyRate);
            System.out.printf("Tổng tiền: %.2f%n", totalCost);
            System.out.printf("Số dư còn lại: %.2f%n", newBalance);
            System.out.println("Trạng thái: PAID");
        }
        
        // Update booking status and save
        selectedBooking.setBookingStatus(BookingStatus.COMPLETED);
        bookingDAO.update(selectedBooking);
    }

    public void viewBookingHistory() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Booking> bookings = bookingDAO.findByCustomerId(currentUser.getUserId());

        System.out.println("\n====================================================================================");
        System.out.println("|                       LỊCH SỬ ĐẶT MÁY                                          |");
        System.out.println("====================================================================================");

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("| Không có lịch sử đặt máy                                                        |");
        } else {
            System.out.printf("| %-15s | %-20s | %-20s | %-15s | %-10s |%n", "Mã đặt", "Máy", "Thời gian bắt đầu", "Tổng tiền", "Trạng thái");
            System.out.println("====================================================================================");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Booking booking : bookings) {
                Workstation ws = workstationDAO.findById(booking.getWorkstationId());
                String startTimeStr = booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A";
                System.out.printf("| %-15s | %-20s | %-20s | %-15.2f | %-10s |%n",
                    booking.getBookingCode(),
                    ws != null ? ws.getStationName() : "N/A",
                    startTimeStr,
                    booking.getTotalAmount(),
                    booking.getBookingStatus().toString());
            }
        }
        System.out.println("====================================================================================\n");
    }

    public void viewUnpaidWorkstations() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Booking> bookings = bookingDAO.findByCustomerId(currentUser.getUserId());
        List<Booking> unpaidBookings = bookings.stream()
            .filter(b -> b.getPaymentStatus() == PaymentStatus.UNPAID && b.getBookingStatus() == BookingStatus.COMPLETED)
            .toList();

        System.out.println("\n===================================================================================");
        System.out.println("|                    MÁY TRẠM CHƯA THANH TOÁN                                     |");
        System.out.println("===================================================================================");

        if (unpaidBookings == null || unpaidBookings.isEmpty()) {
            System.out.println("| Không có máy nào chưa thanh toán                                           |");
        } else {
            System.out.printf("| %-15s | %-20s | %-15s | %-20s |%n", "Mã đặt", "Máy", "Khoản nợ", "Thời gian");
            System.out.println("===================================================================================");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            double totalDebt = 0;
            for (Booking booking : unpaidBookings) {
                Workstation ws = workstationDAO.findById(booking.getWorkstationId());
                String endTimeStr = booking.getEndTime() != null ? booking.getEndTime().format(formatter) : "N/A";
                double debt = booking.getTotalAmount();
                totalDebt += debt;
                System.out.printf("| %-15s | %-20s | %-15.2f | %-20s |%n",
                    booking.getBookingCode(),
                    ws != null ? ws.getStationName() : "N/A",
                    debt,
                    endTimeStr);
            }
            System.out.println("===================================================================================");
            System.out.printf("| %-15s | %-20s | %-15.2f | %-20s |%n", "", "Tổng nợ:", totalDebt, "");
        }
        System.out.println("===================================================================================\n");
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

        if (pendingOrders.isEmpty()) {
            System.out.println("Bạn không có đơn hàng nào đang chờ để hủy.");
            return;
        }

        System.out.println("\n==============================================================================");
        System.out.printf("| %-82s |%n", "DANH SÁCH ĐƠN HÀNG CHỜ HỦY");
        System.out.println("==============================================================================");
        System.out.printf("| %-5s | %-15s | %-15s | %-15s |%n", "STT", "Mã đơn", "Đặt máy", "Tổng tiền");
        System.out.println("==============================================================================");

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
        System.out.println("==============================================================================\n");

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

        System.out.println("\n==============================================================================");
        System.out.printf("| %-82s |%n", "DANH SÁCH ĐƠN HÀNG ĐANG CHỜ");
        System.out.println("==============================================================================");

        if (pendingOrders.isEmpty()) {
            System.out.println("| Không có đơn hàng nào đang chờ                                            |");
        } else {
            System.out.printf("| %-15s | %-15s | %-15s | %-15s |%n", "Mã đơn", "Đặt máy", "Tổng tiền", "Trạng thái");
            System.out.println("==============================================================================");
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
        System.out.println("==============================================================================\n");
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

        System.out.println("\n==============================================================================");
        System.out.printf("| %-82s |%n", "DANH SÁCH ĐƠN HÀNG ĐÃ HỦY");
        System.out.println("==============================================================================");

        if (cancelledOrders == null || cancelledOrders.isEmpty()) {
            System.out.println("| Không có đơn hàng nào đã hủy                                            |");
        } else {
            System.out.printf("| %-15s | %-15s | %-15s | %-15s |%n", "Mã đơn", "Đặt máy", "Tổng tiền", "Trạng thái");
            System.out.println("==============================================================================");
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
        System.out.println("==============================================================================\n");
    }

    public void viewOrderStatus() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Order> orders = orderDAO.findByCustomerId(currentUser.getUserId()).stream()
            .filter(o -> o.getOrderStatus() != OrderStatus.COMPLETED && o.getOrderStatus() != OrderStatus.CANCELLED)
            .toList();

        System.out.println("\n======================================================================");
        System.out.println("|                  TRẠNG THÁI ĐƠN HÀNG HIỆN TẠI                     |");
        System.out.println("======================================================================");

        if (orders == null || orders.isEmpty()) {
            System.out.println("| Không có đơn hàng nào đang xử lý                                   |");
        } else {
            System.out.printf("| %-15s | %-15s | %-15s | %-15s |%n", "Mã đơn", "Đặt máy", "Tổng tiền", "Trạng thái");
            System.out.println("----------------------------------------------------------------------");
            for (Order order : orders) {
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

    public void viewOrderHistory() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Lỗi: Không thể lấy dữ liệu người dùng");
            return;
        }

        List<Order> orders = orderDAO.findByCustomerId(currentUser.getUserId());

        System.out.println("\n======================================================================");
        System.out.println("|                       LỊCH SỬ ĐƠN HÀNG                            |");
        System.out.println("======================================================================");

        if (orders == null || orders.isEmpty()) {
            System.out.println("| Không có lịch sử đơn hàng                                         |");
        } else {
            System.out.printf("| %-15s | %-15s | %-15s | %-15s |%n", "Mã đơn", "Đặt máy", "Tổng tiền", "Trạng thái");
            System.out.println("----------------------------------------------------------------------");
            for (Order order : orders) {
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

    public void addMoney() {
        User currentUser = SessionManager.getCurrentUser();
        int money = InputHandler.inputInt("Nhập số tiền cần nạp: ");

        if (userDAO.addMoney(currentUser.getUserId(), money)) {
            currentUser.setBalance(currentUser.getBalance() + money);
            System.out.println("Nạp tiền thành công");
        }
    }
}
