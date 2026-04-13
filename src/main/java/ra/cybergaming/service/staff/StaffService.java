package ra.cybergaming.service.staff;

import ra.cybergaming.dao.impl.*;
import ra.cybergaming.model.*;
import ra.cybergaming.model.enums.BookingStatus;
import ra.cybergaming.model.enums.OrderStatus;
import ra.cybergaming.model.enums.WorkingStationStatus;
import ra.cybergaming.util.InputHandler;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffService {
    private BookingDAO bookingDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private WorkstationDAO workstationDAO;
    private ServiceDAO serviceDAO;
    private UserDAO userDAO;

    public StaffService() {
        this.bookingDAO = new BookingDAO();
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.workstationDAO = new WorkstationDAO();
        this.serviceDAO = new ServiceDAO();
        this.userDAO = new UserDAO();
    }

    public void acceptPendingBooking() {
        List<Booking> pendingBookings = bookingDAO.search(BookingStatus.PENDING.toString());

        if (pendingBookings == null || pendingBookings.isEmpty()) {
            System.out.println("Không có booking nào đang chờ tiếp nhận.");
            return;
        }

        System.out.println("\n===========================================================================================");
        System.out.printf("| %-89s |%n", "DANH SÁCH BOOKING ĐANG CHỜ TIẾP NHẬN");
        System.out.println("===========================================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s | %-20s |%n", 
            "STT", "Mã đặt", "Khách hàng", "Máy", "Thời gian bắt đầu");
        System.out.println("-------------------------------------------------------------------------------------------");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int i = 0; i < pendingBookings.size(); i++) {
            Booking booking = pendingBookings.get(i);
            User customer = userDAO.findById(booking.getCustomerId());
            Workstation ws = workstationDAO.findById(booking.getWorkstationId());
            String customerName = customer != null ? customer.getFullName() : "N/A";
            String wsName = ws != null ? ws.getStationName() : "N/A";
            String startTime = booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A";

            System.out.printf("| %-5d | %-15s | %-20s | %-15s | %-20s |%n",
                i + 1, booking.getBookingCode(), customerName, wsName, startTime);
        }
        System.out.println("===========================================================================================\n");

        int choice = InputHandler.inputInt("Chọn booking cần tiếp nhận (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > pendingBookings.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Booking selectedBooking = pendingBookings.get(choice - 1);

        selectedBooking.setBookingStatus(BookingStatus.ACTIVE);
        
        Workstation workstation = workstationDAO.findById(selectedBooking.getWorkstationId());
        if (workstation != null) {
            workstation.setStatus(WorkingStationStatus.IN_USE);
            workstationDAO.update(workstation);
        }

        if (bookingDAO.update(selectedBooking)) {
            System.out.println("\nTiếp nhận booking thành công!");
            System.out.println("Trạng thái booking: ACTIVE");
            System.out.println("Trạng thái máy: IN_USE");
        } else {
            System.out.println("\nTiếp nhận booking thất bại. Vui lòng thử lại.");
        }
    }

    public void updateBookingStatus() {
        List<Booking> activeBookings = bookingDAO.search(BookingStatus.ACTIVE.toString());

        if (activeBookings == null || activeBookings.isEmpty()) {
            System.out.println("Không có booking nào đang hoạt động để cập nhật.");
            return;
        }

        System.out.println("\n===========================================================================================");
        System.out.printf("| %-89s |%n", "DANH SÁCH BOOKING ĐANG HOẠT ĐỘNG");
        System.out.println("===========================================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s | %-20s |%n", 
            "STT", "Mã đặt", "Khách hàng", "Máy", "Thời gian bắt đầu");
        System.out.println("-------------------------------------------------------------------------------------------");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int i = 0; i < activeBookings.size(); i++) {
            Booking booking = activeBookings.get(i);
            User customer = userDAO.findById(booking.getCustomerId());
            Workstation ws = workstationDAO.findById(booking.getWorkstationId());
            String customerName = customer != null ? customer.getFullName() : "N/A";
            String wsName = ws != null ? ws.getStationName() : "N/A";
            String startTime = booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A";

            System.out.printf("| %-5d | %-15s | %-20s | %-15s | %-20s |%n",
                i + 1, booking.getBookingCode(), customerName, wsName, startTime);
        }
        System.out.println("===========================================================================================\n");

        int choice = InputHandler.inputInt("Chọn booking cần cập nhật trạng thái (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > activeBookings.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Booking selectedBooking = activeBookings.get(choice - 1);

        System.out.println("\n+======================================+");
        System.out.println("| Chọn trạng thái mới:                |");
        System.out.println("+======================================+");
        System.out.println("| 1. COMPLETED                        |");
        System.out.println("| 2. CANCELLED                        |");
        System.out.println("+======================================+");

        int statusChoice = InputHandler.inputInt("Chọn: ");

        BookingStatus newStatus = null;
        switch (statusChoice) {
            case 1:
                newStatus = BookingStatus.COMPLETED;
                break;
            case 2:
                newStatus = BookingStatus.CANCELLED;
                break;
            default:
                System.out.println("Lỗi: Lựa chọn không hợp lệ");
                return;
        }

        selectedBooking.setBookingStatus(newStatus);

        if (newStatus == BookingStatus.COMPLETED || newStatus == BookingStatus.CANCELLED) {
            Workstation workstation = workstationDAO.findById(selectedBooking.getWorkstationId());
            if (workstation != null) {
                workstation.setStatus(WorkingStationStatus.AVAILABLE);
                workstationDAO.update(workstation);
            }
        }

        if (bookingDAO.update(selectedBooking)) {
            System.out.println("\nCập nhật trạng thái booking thành công!");
            System.out.println("Trạng thái mới: " + newStatus);
        } else {
            System.out.println("\nCập nhật trạng thái booking thất bại. Vui lòng thử lại.");
        }
    }

    public void acceptPendingOrder() {
        List<Order> pendingOrders = orderDAO.findByStatus(OrderStatus.PENDING);

        if (pendingOrders == null || pendingOrders.isEmpty()) {
            System.out.println("Không có order nào đang chờ tiếp nhận.");
            return;
        }

        System.out.println("\n=================================================================================");
        System.out.printf("| %-89s |%n", "DANH SÁCH ORDER ĐANG CHỜ TIẾP NHẬN");
        System.out.println("=================================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s | %-15s |%n", 
            "STT", "Mã order", "Khách hàng", "Tổng tiền", "Thời gian");
        System.out.println("---------------------------------------------------------------------------------");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int i = 0; i < pendingOrders.size(); i++) {
            Order order = pendingOrders.get(i);
            User customer = userDAO.findById(order.getCustomerId());
            String customerName = customer != null ? customer.getFullName() : "N/A";
            String createdTime = order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : "N/A";

            System.out.printf("| %-5d | %-15s | %-20s | %-15.2f | %-15s |%n",
                i + 1, order.getOrderCode(), customerName, order.getTotalAmount(), createdTime);
        }
        System.out.println("=================================================================================\n");

        int choice = InputHandler.inputInt("Chọn order cần tiếp nhận (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > pendingOrders.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Order selectedOrder = pendingOrders.get(choice - 1);

        List<OrderItem> orderItems = orderItemDAO.findByOrderId(selectedOrder.getOrderId());

        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderItem item : orderItems) {
                Service service = serviceDAO.findById(item.getServiceId());
                if (service != null) {
                    int newQuantity = service.getStock_quantity() - item.getQuantity();
                    if (newQuantity < 0) {
                        System.out.printf("Không đủ hàng cho dịch vụ: %s%n", service.getServiceName());
                        return;
                    }
                    service.setStock_quantity(newQuantity);
                    serviceDAO.update(service);
                }
            }
        }

        selectedOrder.setOrderStatus(OrderStatus.CONFIRMED);

        if (orderDAO.update(selectedOrder)) {
            System.out.println("\nTiếp nhận order thành công!");
            System.out.println("Trạng thái order: CONFIRMED");
            System.out.println("Số lượng dịch vụ đã trừ.");
        } else {
            System.out.println("\nTiếp nhận order thất bại. Vui lòng thử lại.");
        }
    }

    public void updateOrderStatus() {
        List<Order> confirmedOrders = orderDAO.findByStatus(OrderStatus.CONFIRMED);

        if (confirmedOrders == null || confirmedOrders.isEmpty()) {
            System.out.println("Không có order nào đang xác nhận để cập nhật.");
            return;
        }

        System.out.println("\n=================================================================================");
        System.out.printf("| %-89s |%n", "DANH SÁCH ORDER ĐANG XÁC NHẬN");
        System.out.println("=================================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s | %-15s |%n", 
            "STT", "Mã order", "Khách hàng", "Tổng tiền", "Thời gian");
        System.out.println("---------------------------------------------------------------------------------");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int i = 0; i < confirmedOrders.size(); i++) {
            Order order = confirmedOrders.get(i);
            User customer = userDAO.findById(order.getCustomerId());
            String customerName = customer != null ? customer.getFullName() : "N/A";
            String createdTime = order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : "N/A";

            System.out.printf("| %-5d | %-15s | %-20s | %-15.2f | %-15s |%n",
                i + 1, order.getOrderCode(), customerName, order.getTotalAmount(), createdTime);
        }
        System.out.println("=================================================================================\n");

        int choice = InputHandler.inputInt("Chọn order cần cập nhật trạng thái (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > confirmedOrders.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Order selectedOrder = confirmedOrders.get(choice - 1);

        System.out.println("\n+======================================+");
        System.out.println("| Chọn trạng thái mới:                |");
        System.out.println("+======================================+");
        System.out.println("| 1. COMPLETED                        |");
        System.out.println("| 2. CANCELLED                        |");
        System.out.println("+======================================+");

        int statusChoice = InputHandler.inputInt("Chọn: ");

        OrderStatus newStatus = null;
        switch (statusChoice) {
            case 1:
                newStatus = OrderStatus.COMPLETED;
                break;
            case 2:
                newStatus = OrderStatus.CANCELLED;
                break;
            default:
                System.out.println("Lỗi: Lựa chọn không hợp lệ");
                return;
        }

        selectedOrder.setOrderStatus(newStatus);

        if (orderDAO.update(selectedOrder)) {
            System.out.println("\nCập nhật trạng thái order thành công!");
            System.out.println("Trạng thái mới: " + newStatus);
        } else {
            System.out.println("\nCập nhật trạng thái order thất bại. Vui lòng thử lại.");
        }
    }
}
