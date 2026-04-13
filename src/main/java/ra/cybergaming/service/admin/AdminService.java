package ra.cybergaming.service.admin;

import ra.cybergaming.dao.impl.*;
import ra.cybergaming.model.*;
import ra.cybergaming.model.enums.*;
import ra.cybergaming.util.InputHandler;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminService {
    private WorkstationDAO workstationDAO;
    private ServiceDAO serviceDAO;
    private AreaDAO areaDAO;
    private UserDAO userDAO;
    private BookingDAO bookingDAO;
    private OrderDAO orderDAO;

    public AdminService() {
        this.workstationDAO = new WorkstationDAO();
        this.serviceDAO = new ServiceDAO();
        this.areaDAO = new AreaDAO();
        this.userDAO = new UserDAO();
        this.bookingDAO = new BookingDAO();
        this.orderDAO = new OrderDAO();
    }

    public void displayWorkstations() {
        List<Workstation> workstations = workstationDAO.findAll();

        if (workstations == null || workstations.isEmpty()) {
            System.out.println("Không có máy trạm nào trong hệ thống.");
            return;
        }

        System.out.println("\n===================================================================================");
        System.out.printf("| %-79s |%n", "DANH SÁCH MÁY TRẠM");
        System.out.println("===================================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s | %-12s |%n", 
            "ID", "Mã máy", "Tên máy", "Giá/giờ", "Trạng thái");
        System.out.println("-----------------------------------------------------------------------------------");

        for (Workstation ws : workstations) {
            System.out.printf("| %-5d | %-15s | %-20s | %-15.2f | %-12s |%n",
                ws.getWorkstationId(), ws.getStationCode(), ws.getStationName(), 
                ws.getHourlyRate(), ws.getStatus());
        }
        System.out.println("===================================================================================\n");
    }

    public void addWorkstation() {
        List<Area> areas = areaDAO.findAll();
        
        if (areas == null || areas.isEmpty()) {
            System.out.println("Lỗi: Không có phòng máy nào. Vui lòng tạo phòng máy trước.");
            return;
        }

        System.out.println("\n+======================================+");
        System.out.println("| THÊM MÁY TRẠM MỚI                   |");
        System.out.println("+======================================+");

        String name = InputHandler.inputString("Nhập tên máy: ");
        double hourlyRate = InputHandler.inputDouble("Nhập giá tiền/giờ: ");
        String specification = InputHandler.inputString("Nhập thông số kỹ thuật: ");

        System.out.println("\nDanh sách phòng máy:");
        for (int i = 0; i < areas.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, areas.get(i).getAreaName());
        }
        int areaChoice = InputHandler.inputInt("Chọn phòng máy (1-" + areas.size() + "): ");

        if (areaChoice < 1 || areaChoice > areas.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Workstation workstation = new Workstation();
        workstation.setStationName(name);
        workstation.setHourlyRate(hourlyRate);
        workstation.setSpecification(specification);
        workstation.setAreaId(areas.get(areaChoice - 1).getAreaId());
        workstation.setStatus(WorkingStationStatus.AVAILABLE);

        if (workstationDAO.create(workstation)) {
            System.out.println("Thêm máy trạm thành công!");
        } else {
            System.out.println("Thêm máy trạm thất bại. Vui lòng thử lại.");
        }
    }

    public void updateWorkstation() {
        List<Workstation> workstations = workstationDAO.findAll();

        if (workstations == null || workstations.isEmpty()) {
            System.out.println("Không có máy trạm nào để cập nhật.");
            return;
        }

        System.out.println("\n====================================================================");
        System.out.printf("| %-64s |%n", "DANH SÁCH MÁY TRẠM");
        System.out.println("====================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s |%n", 
            "STT", "Mã máy", "Tên máy", "Giá/giờ");
        System.out.println("--------------------------------------------------------------------");

        for (int i = 0; i < workstations.size(); i++) {
            Workstation ws = workstations.get(i);
            System.out.printf("| %-5d | %-15s | %-20s | %-15.2f |%n",
                i + 1, ws.getStationCode(), ws.getStationName(), ws.getHourlyRate());
        }
        System.out.println("====================================================================\n");

        int choice = InputHandler.inputInt("Chọn máy trạm cần cập nhật (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > workstations.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Workstation selectedWs = workstations.get(choice - 1);

        System.out.println("\n+======================================+");
        System.out.println("| Chọn trường cần cập nhật:           |");
        System.out.println("+======================================+");
        System.out.println("| 1. Tên máy                          |");
        System.out.println("| 2. Giá tiền/giờ                    |");
        System.out.println("| 3. Thông số kỹ thuật               |");
        System.out.println("| 4. Trạng thái máy                  |");
        System.out.println("+======================================+");

        int fieldChoice = InputHandler.inputInt("Chọn: ");

        switch (fieldChoice) {
            case 1:
                String newName = InputHandler.inputString("Nhập tên máy mới: ");
                selectedWs.setStationName(newName);
                break;
            case 2:
                double newRate = InputHandler.inputDouble("Nhập giá tiền/giờ mới: ");
                selectedWs.setHourlyRate(newRate);
                break;
            case 3:
                String newSpec = InputHandler.inputString("Nhập thông số kỹ thuật mới: ");
                selectedWs.setSpecification(newSpec);
                break;
            case 4:
                System.out.println("\n1. AVAILABLE (Có sẵn)");
                System.out.println("2. IN_USE (Đang sử dụng)");
                System.out.println("3. MAINTENANCE (Bảo trì)");
                int statusChoice = InputHandler.inputInt("Chọn: ");
                switch (statusChoice) {
                    case 1: selectedWs.setStatus(WorkingStationStatus.AVAILABLE); break;
                    case 2: selectedWs.setStatus(WorkingStationStatus.IN_USE); break;
                    case 3: selectedWs.setStatus(WorkingStationStatus.MAINTENANCE); break;
                    default: System.out.println("Lỗi: Lựa chọn không hợp lệ"); return;
                }
                break;
            default:
                System.out.println("Lỗi: Lựa chọn không hợp lệ");
                return;
        }

        if (workstationDAO.update(selectedWs)) {
            System.out.println("Cập nhật máy trạm thành công!");
        } else {
            System.out.println("Cập nhật máy trạm thất bại. Vui lòng thử lại.");
        }
    }

    public void deleteWorkstation() {
        List<Workstation> workstations = workstationDAO.findAll();

        if (workstations == null || workstations.isEmpty()) {
            System.out.println("Không có máy trạm nào để xóa.");
            return;
        }

        System.out.println("\n====================================================================");
        System.out.printf("| %-64s |%n", "DANH SÁCH MÁY TRẠM");
        System.out.println("====================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s |%n", 
            "STT", "Mã máy", "Tên máy", "Giá/giờ");
        System.out.println("--------------------------------------------------------------------");

        for (int i = 0; i < workstations.size(); i++) {
            Workstation ws = workstations.get(i);
            System.out.printf("| %-5d | %-15s | %-20s | %-15.2f |%n",
                i + 1, ws.getStationCode(), ws.getStationName(), ws.getHourlyRate());
        }
        System.out.println("====================================================================\n");

        int choice = InputHandler.inputInt("Chọn máy trạm cần xóa (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > workstations.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Workstation selectedWs = workstations.get(choice - 1);
        String confirm = InputHandler.inputString("Bạn có chắc chắn muốn xóa? (Y/N): ");

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Đã hủy xóa máy trạm.");
            return;
        }

        if (workstationDAO.delete(selectedWs.getWorkstationId())) {
            System.out.println("Xóa máy trạm thành công!");
        } else {
            System.out.println("Xóa máy trạm thất bại. Vui lòng thử lại.");
        }
    }


    public void displayServices() {
        List<Service> services = serviceDAO.findAll();

        if (services == null || services.isEmpty()) {
            System.out.println("Không có dịch vụ nào trong hệ thống.");
            return;
        }

        System.out.println("\n============================================================================");
        System.out.printf("| %-72s |%n", "DANH SÁCH DỊCH VỤ F&B");
        System.out.println("============================================================================");
        System.out.printf("| %-5s | %-15s | %-15s | %-15s | %-10s |%n", 
            "ID", "Mã dịch vụ", "Tên dịch vụ", "Giá", "Số lượng");
        System.out.println("----------------------------------------------------------------------------");

        for (Service service : services) {
            System.out.printf("| %-5d | %-15s | %-15s | %-15.2f | %-10d |%n",
                service.getServiceId(), service.getServiceCode(), service.getServiceName(), 
                service.getPrice(), service.getStock_quantity());
        }
        System.out.println("============================================================================\n");
    }

    public void addService() {
        System.out.println("\n+======================================+");
        System.out.println("| THÊM DỊCH VỤ MỚI                    |");
        System.out.println("+======================================+");

        String name = InputHandler.inputString("Nhập tên dịch vụ: ");
        
        System.out.println("\nChọn danh mục:");
        System.out.println("1. FOOD (Đồ ăn)");
        System.out.println("2. DRINK (Thức uống)");
        int categoryChoice = InputHandler.inputInt("Chọn: ");
        
        CategoryType category = categoryChoice == 1 ? CategoryType.FOOD : CategoryType.DRINK;
        
        String description = InputHandler.inputString("Nhập mô tả: ");
        double price = InputHandler.inputDouble("Nhập giá: ");
        int quantity = InputHandler.inputInt("Nhập số lượng: ");

        Service service = new Service();
        service.setServiceName(name);
        service.setCategory(category);
        service.setDescription(description);
        service.setPrice(price);
        service.setStock_quantity(quantity);
        service.setStatus(ServiceStatus.ACTIVE);

        if (serviceDAO.create(service)) {
            System.out.println("Thêm dịch vụ thành công!");
        } else {
            System.out.println("Thêm dịch vụ thất bại. Vui lòng thử lại.");
        }
    }

    public void updateService() {
        List<Service> services = serviceDAO.findAll();

        if (services == null || services.isEmpty()) {
            System.out.println("Không có dịch vụ nào để cập nhật.");
            return;
        }

        System.out.println("\n============================================================================");
        System.out.printf("| %-72s |%n", "DANH SÁCH DỊCH VỤ F&B");
        System.out.println("============================================================================");
        System.out.printf("| %-5s | %-15s | %-15s | %-15s | %-10s |%n", 
            "STT", "Mã dịch vụ", "Tên dịch vụ", "Giá", "Số lượng");
        System.out.println("----------------------------------------------------------------------------");

        for (int i = 0; i < services.size(); i++) {
            Service s = services.get(i);
            System.out.printf("| %-5d | %-15s | %-15s | %-15.2f | %-10d |%n",
                i + 1, s.getServiceCode(), s.getServiceName(), s.getPrice(), s.getStock_quantity());
        }
        System.out.println("============================================================================\n");

        int choice = InputHandler.inputInt("Chọn dịch vụ cần cập nhật (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > services.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Service selectedService = services.get(choice - 1);

        System.out.println("\n+======================================+");
        System.out.println("| Chọn trường cần cập nhật:           |");
        System.out.println("+======================================+");
        System.out.println("| 1. Tên dịch vụ                      |");
        System.out.println("| 2. Giá                              |");
        System.out.println("| 3. Mô tả                            |");
        System.out.println("| 4. Số lượng                         |");
        System.out.println("+======================================+");

        int fieldChoice = InputHandler.inputInt("Chọn: ");

        switch (fieldChoice) {
            case 1:
                String newName = InputHandler.inputString("Nhập tên dịch vụ mới: ");
                selectedService.setServiceName(newName);
                break;
            case 2:
                double newPrice = InputHandler.inputDouble("Nhập giá mới: ");
                selectedService.setPrice(newPrice);
                break;
            case 3:
                String newDesc = InputHandler.inputString("Nhập mô tả mới: ");
                selectedService.setDescription(newDesc);
                break;
            case 4:
                int newQuantity = InputHandler.inputInt("Nhập số lượng mới: ");
                selectedService.setStock_quantity(newQuantity);
                break;
            default:
                System.out.println("Lỗi: Lựa chọn không hợp lệ");
                return;
        }

        if (serviceDAO.update(selectedService)) {
            System.out.println("Cập nhật dịch vụ thành công!");
        } else {
            System.out.println("Cập nhật dịch vụ thất bại. Vui lòng thử lại.");
        }
    }

    public void deleteService() {
        List<Service> services = serviceDAO.findAll();

        if (services == null || services.isEmpty()) {
            System.out.println("Không có dịch vụ nào để xóa.");
            return;
        }

        System.out.println("\n============================================================================");
        System.out.printf("| %-72s |%n", "DANH SÁCH DỊCH VỤ F&B");
        System.out.println("============================================================================");
        System.out.printf("| %-5s | %-15s | %-15s | %-15s | %-10s |%n", 
            "STT", "Mã dịch vụ", "Tên dịch vụ", "Giá", "Số lượng");
        System.out.println("----------------------------------------------------------------------------");

        for (int i = 0; i < services.size(); i++) {
            Service s = services.get(i);
            System.out.printf("| %-5d | %-15s | %-15s | %-15.2f | %-10d |%n",
                i + 1, s.getServiceCode(), s.getServiceName(), s.getPrice(), s.getStock_quantity());
        }
        System.out.println("============================================================================\n");

        int choice = InputHandler.inputInt("Chọn dịch vụ cần xóa (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > services.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Service selectedService = services.get(choice - 1);
        String confirm = InputHandler.inputString("Bạn có chắc chắn muốn xóa? (Y/N): ");

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Đã hủy xóa dịch vụ.");
            return;
        }

        if (serviceDAO.delete(selectedService.getServiceId())) {
            System.out.println("Xóa dịch vụ thành công!");
        } else {
            System.out.println("Xóa dịch vụ thất bại. Vui lòng thử lại.");
        }
    }


    public void displayAreas() {
        List<Area> areas = areaDAO.findAll();

        if (areas == null || areas.isEmpty()) {
            System.out.println("Không có phòng máy nào trong hệ thống.");
            return;
        }

        System.out.println("\n======================================================================");
        System.out.printf("| %-66s |%n", "DANH SÁCH PHÒNG MÁY");
        System.out.println("======================================================================");
        System.out.printf("| %-5s | %-25s | %-30s |%n", 
            "ID", "Tên phòng", "Mô tả");
        System.out.println("----------------------------------------------------------------------");

        for (Area area : areas) {
            System.out.printf("| %-5d | %-25s | %-30s |%n",
                area.getAreaId(), area.getAreaName(), area.getDescription() != null ? area.getDescription() : "");
        }
        System.out.println("======================================================================\n");
    }

    public void addArea() {
        System.out.println("\n+======================================+");
        System.out.println("| THÊM PHÒNG MÁY MỚI                  |");
        System.out.println("+======================================+");

        String name = InputHandler.inputString("Nhập tên phòng máy: ");
        String description = InputHandler.inputString("Nhập mô tả: ");

        Area area = new Area();
        area.setAreaName(name);
        area.setDescription(description);

        if (areaDAO.create(area)) {
            System.out.println("Thêm phòng máy thành công!");
        } else {
            System.out.println("Thêm phòng máy thất bại. Vui lòng thử lại.");
        }
    }

    public void updateArea() {
        List<Area> areas = areaDAO.findAll();

        if (areas == null || areas.isEmpty()) {
            System.out.println("Không có phòng máy nào để cập nhật.");
            return;
        }

        System.out.println("\n==========================================");
        System.out.printf("| %-38s |%n", "DANH SÁCH PHÒNG MÁY");
        System.out.println("==========================================");
        System.out.printf("| %-5s | %-30s |%n", 
            "STT", "Tên phòng");
        System.out.println("------------------------------------------");

        for (int i = 0; i < areas.size(); i++) {
            Area area = areas.get(i);
            System.out.printf("| %-5d | %-30s |%n",
                i + 1, area.getAreaName());
        }
        System.out.println("===========================================\n");

        int choice = InputHandler.inputInt("Chọn phòng máy cần cập nhật (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > areas.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Area selectedArea = areas.get(choice - 1);

        System.out.println("\n+======================================+");
        System.out.println("| Chọn trường cần cập nhật:           |");
        System.out.println("+======================================+");
        System.out.println("| 1. Tên phòng                        |");
        System.out.println("| 2. Mô tả                            |");
        System.out.println("+======================================+");

        int fieldChoice = InputHandler.inputInt("Chọn: ");

        switch (fieldChoice) {
            case 1:
                String newName = InputHandler.inputString("Nhập tên phòng mới: ");
                selectedArea.setAreaName(newName);
                break;
            case 2:
                String newDescription = InputHandler.inputString("Nhập mô tả mới: ");
                selectedArea.setDescription(newDescription);
                break;
            default:
                System.out.println("Lỗi: Lựa chọn không hợp lệ");
                return;
        }

        if (areaDAO.update(selectedArea)) {
            System.out.println("Cập nhật phòng máy thành công!");
        } else {
            System.out.println("Cập nhật phòng máy thất bại. Vui lòng thử lại.");
        }
    }

    public void deleteArea() {
        List<Area> areas = areaDAO.findAll();

        if (areas == null || areas.isEmpty()) {
            System.out.println("Không có phòng máy nào để xóa.");
            return;
        }

        System.out.println("\n==========================================");
        System.out.printf("| %-38s |%n", "DANH SÁCH PHÒNG MÁY");
        System.out.println("==========================================");
        System.out.printf("| %-5s | %-30s |%n", 
            "STT", "Tên phòng");
        System.out.println("------------------------------------------");
        for (int i = 0; i < areas.size(); i++) {
            Area area = areas.get(i);
            System.out.printf("| %-5d | %-30s |%n",
                i + 1, area.getAreaName());
        }
        System.out.println("===========================================\n");

        int choice = InputHandler.inputInt("Chọn phòng máy cần xóa (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > areas.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        Area selectedArea = areas.get(choice - 1);
        String confirm = InputHandler.inputString("Bạn có chắc chắn muốn xóa? (Y/N): ");

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Đã hủy xóa phòng máy.");
            return;
        }

        if (areaDAO.delete(selectedArea.getAreaId())) {
            System.out.println("Xóa phòng máy thành công!");
        } else {
            System.out.println("Xóa phòng máy thất bại. Vui lòng thử lại.");
        }
    }


    public void displayUsers() {
        List<User> users = userDAO.findAll();

        if (users == null || users.isEmpty()) {
            System.out.println("Không có người dùng nào trong hệ thống.");
            return;
        }

        System.out.println("\n======================================================================================");
        System.out.printf("| %-82s |%n", "DANH SÁCH NGƯỜI DÙNG");
        System.out.println("======================================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s | %-15s |%n", 
            "ID", "Tên đăng nhập", "Tên đầy đủ", "Vai trò", "Trạng thái");
        System.out.println("--------------------------------------------------------------------------------------");

        for (User user : users) {
            String roleName = user.getRoleType() != null ? user.getRoleType().toString() : "N/A";
            String statusName = user.getStatus() != null ? user.getStatus().toString() : "N/A";
            System.out.printf("| %-5d | %-15s | %-20s | %-15s | %-15s |%n",
                user.getUserId(), user.getUsername(), user.getFullName(), roleName, statusName);
        }
        System.out.println("======================================================================================\n");
    }

    public void changeUserRole() {
        List<User> users = userDAO.findAll();

        if (users == null || users.isEmpty()) {
            System.out.println("Không có người dùng nào để thay đổi vai trò.");
            return;
        }

        System.out.println("\n====================================================================");
        System.out.printf("| %-64s |%n", "DANH SÁCH NGƯỜI DÙNG");
        System.out.println("====================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s |%n", 
            "STT", "Tên đăng nhập", "Tên đầy đủ", "Vai trò");
        System.out.println("--------------------------------------------------------------------");

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            String roleName = user.getRoleType() != null ? user.getRoleType().toString() : "N/A";
            System.out.printf("| %-5d | %-15s | %-20s | %-15s |%n",
                i + 1, user.getUsername(), user.getFullName(), roleName);
        }
        System.out.println("====================================================================\n");

        int choice = InputHandler.inputInt("Chọn người dùng cần thay đổi vai trò (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > users.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        User selectedUser = users.get(choice - 1);

        if (!selectedUser.getRoleType().equals(RoleType.CUSTOMER) && !selectedUser.getRoleType().equals(RoleType.STAFF)) {
            System.out.println("Chỉ có thể thay đổi vai trò cho CUSTOMER và STAFF.");
            return;
        }

        System.out.println("\n+======================================+");
        System.out.println("| Chọn vai trò mới:                   |");
        System.out.println("+======================================+");
        System.out.println("| 1. CUSTOMER                         |");
        System.out.println("| 2. STAFF                            |");
        System.out.println("+======================================+");

        int roleChoice = InputHandler.inputInt("Chọn: ");

        RoleType newRole = null;
        switch (roleChoice) {
            case 1:
                newRole = RoleType.CUSTOMER;
                break;
            case 2:
                newRole = RoleType.STAFF;
                break;
            default:
                System.out.println("Lỗi: Lựa chọn không hợp lệ");
                return;
        }

        selectedUser.setRoleType(newRole);

        if (userDAO.update(selectedUser)) {
            System.out.println("Thay đổi vai trò thành công!");
            System.out.println("Vai trò mới: " + newRole);
        } else {
            System.out.println("Thay đổi vai trò thất bại. Vui lòng thử lại.");
        }
    }

    public void lockUserAccount() {
        List<User> users = userDAO.findAll();

        if (users == null || users.isEmpty()) {
            System.out.println("Không có người dùng nào để cấm.");
            return;
        }

        System.out.println("\n====================================================================");
        System.out.printf("| %-64s |%n", "DANH SÁCH NGƯỜI DÙNG");
        System.out.println("====================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s |%n", 
            "STT", "Tên đăng nhập", "Tên đầy đủ", "Vai trò");
        System.out.println("--------------------------------------------------------------------");

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            String statusName = user.getStatus() != null ? user.getStatus().toString() : "N/A";
            System.out.printf("| %-5d | %-15s | %-20s | %-15s |%n",
                i + 1, user.getUsername(), user.getFullName(), statusName);
        }
        System.out.println("====================================================================\n");

        int choice = InputHandler.inputInt("Chọn người dùng cần cấm (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > users.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        User selectedUser = users.get(choice - 1);

        if (selectedUser.getStatus() == UserStatus.LOCKED) {
            System.out.println("Tài khoản này đã bị cấm rồi.");
            return;
        }

        String confirm = InputHandler.inputString("Bạn có chắc chắn muốn cấm tài khoản này? (Y/N): ");

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Đã hủy cấm tài khoản.");
            return;
        }

        selectedUser.setStatus(UserStatus.LOCKED);

        if (userDAO.update(selectedUser)) {
            System.out.println("Cấm tài khoản thành công!");
            System.out.println("Trạng thái: " + UserStatus.LOCKED);
        } else {
            System.out.println("Cấm tài khoản thất bại. Vui lòng thử lại.");
        }
    }

    public void unlockUserAccount() {
        List<User> users = userDAO.findAll();

        if (users == null || users.isEmpty()) {
            System.out.println("Không có người dùng nào để mở khoá.");
            return;
        }

        List<User> lockedUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getStatus() == UserStatus.LOCKED) {
                lockedUsers.add(user);
            }
        }

        if (lockedUsers.isEmpty()) {
            System.out.println("Không có tài khoản nào bị cấm.");
            return;
        }

        System.out.println("\n====================================================================");
        System.out.printf("| %-64s |%n", "DANH SÁCH NGƯỜI DÙNG");
        System.out.println("====================================================================");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s |%n", 
            "STT", "Tên đăng nhập", "Tên đầy đủ", "Vai trò");
        System.out.println("--------------------------------------------------------------------");

        for (int i = 0; i < lockedUsers.size(); i++) {
            User user = lockedUsers.get(i);
            System.out.printf("| %-5d | %-15s | %-20s | %-15s |%n",
                i + 1, user.getUsername(), user.getFullName(), user.getStatus());
        }
        System.out.println("====================================================================\n");

        int choice = InputHandler.inputInt("Chọn người dùng cần mở khoá (0 để hủy): ");

        if (choice == 0) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        if (choice < 1 || choice > lockedUsers.size()) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ");
            return;
        }

        User selectedUser = lockedUsers.get(choice - 1);

        String confirm = InputHandler.inputString("Bạn có chắc chắn muốn mở khoá tài khoản này? (Y/N): ");

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Đã hủy mở khoá tài khoản.");
            return;
        }

        selectedUser.setStatus(UserStatus.ACTIVE);

        if (userDAO.update(selectedUser)) {
            System.out.println("Mở khoá tài khoản thành công!");
            System.out.println("Trạng thái: " + UserStatus.ACTIVE);
        } else {
            System.out.println("Mở khoá tài khoản thất bại. Vui lòng thử lại.");
        }
    }

    public void exportDailyRevenueToExcel(String dateInput) {
        LocalDateTime reportDate;
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            if (dateInput.isEmpty()) {
                reportDate = LocalDateTime.now();
            } else {
                reportDate = LocalDateTime.parse(dateInput + " 00:00:00", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            }
        } catch (Exception e) {
            System.out.println("Định dạng ngày không hợp lệ. Vui lòng nhập lại (dd/MM/yyyy)");
            return;
        }


        LocalDateTime dayStart = reportDate.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime dayEnd = dayStart.plusDays(1);

        List<Booking> bookings = bookingDAO.findAll().stream()
                .filter(b -> Optional.ofNullable(b.getCreatedAt())
                        .map(createdAt -> createdAt.isAfter(dayStart.minusSeconds(1)) && createdAt.isBefore(dayEnd)
                                && b.getPaymentStatus() == PaymentStatus.PAID)
                        .orElse(false))
                .toList();

        List<Order> orders = orderDAO.findAll().stream()
                .filter(o -> Optional.ofNullable(o.getCreatedAt())
                        .map(createdAt -> createdAt.isAfter(dayStart.minusSeconds(1)) && createdAt.isBefore(dayEnd)
                                && o.getOrderStatus() == OrderStatus.COMPLETED)
                        .orElse(false))
                .toList();

        double bookingRevenue = bookings.stream().mapToDouble(Booking::getTotalAmount).sum();
        double orderRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        double totalRevenue = bookingRevenue + orderRevenue;

        System.out.println(bookingRevenue);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo Cáo Doanh Thu");

            int rowNum = 0;
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO DOANH THU NGÀY " + dateFormat.format(reportDate));
            titleCell.setCellStyle(createHeaderStyle(workbook));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            rowNum++;
            Row summaryRow = sheet.createRow(rowNum++);
            summaryRow.createCell(0).setCellValue("Tổng doanh thu máy tính:");
            summaryRow.createCell(1).setCellValue(bookingRevenue);

            Row orderRow = sheet.createRow(rowNum++);
            orderRow.createCell(0).setCellValue("Tổng doanh thu đồ ăn/thức uống:");
            orderRow.createCell(1).setCellValue(orderRevenue);

            Row totalRow = sheet.createRow(rowNum++);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TỔNG DOANH THU:");
            totalLabelCell.setCellStyle(createHeaderStyle(workbook));
            Cell totalValueCell = totalRow.createCell(1);
            totalValueCell.setCellValue(totalRevenue);
            totalValueCell.setCellStyle(createHeaderStyle(workbook));

            rowNum += 2;
            Row bookingHeaderRow = sheet.createRow(rowNum++);
            String[] bookingHeaders = {"STT", "Mã Booking", "Khách Hàng", "Máy Trạm", "Thời Gian Bắt Đầu", "Thời Gian Kết Thúc", "Doanh Thu"};
            for (int i = 0; i < bookingHeaders.length; i++) {
                Cell cell = bookingHeaderRow.createCell(i);
                cell.setCellValue(bookingHeaders[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            int stt = 1;
            for (Booking booking : bookings) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stt++);
                row.createCell(1).setCellValue(booking.getBookingCode() != null ? booking.getBookingCode() : "");
                User customer = userDAO.findById(booking.getCustomerId());
                row.createCell(2).setCellValue(customer != null ? customer.getFullName() : "");
                Workstation ws = workstationDAO.findById(booking.getWorkstationId());
                row.createCell(3).setCellValue(ws != null ? ws.getStationName() : "");
                row.createCell(4).setCellValue(booking.getStartTime() != null ? booking.getStartTime().toString() : "");
                row.createCell(5).setCellValue(booking.getEndTime() != null ? booking.getEndTime().toString() : "");
                row.createCell(6).setCellValue(booking.getTotalAmount());
            }

            if (!orders.isEmpty()) {
                rowNum += 2;
                Row orderHeaderRow = sheet.createRow(rowNum++);
                String[] orderHeaders = {"STT", "Mã Order", "Khách Hàng", "Tổng Tiền", "Thời Gian"};
                for (int i = 0; i < orderHeaders.length; i++) {
                    Cell cell = orderHeaderRow.createCell(i);
                    cell.setCellValue(orderHeaders[i]);
                    cell.setCellStyle(createHeaderStyle(workbook));
                }

                stt = 1;
                for (Order order : orders) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(stt++);
                    row.createCell(1).setCellValue(order.getOrderCode() != null ? order.getOrderCode() : "");
                    User customer = userDAO.findById(order.getCustomerId());
                    row.createCell(2).setCellValue(customer != null ? customer.getFullName() : "");
                    row.createCell(3).setCellValue(order.getTotalAmount());
                    row.createCell(4).setCellValue(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "");
                }
            }

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            String filePath = "reports/revenue_" + dateFormat.format(reportDate).replace("/", "-") + "_" + System.currentTimeMillis() + ".xlsx";
            new File("reports").mkdirs();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                System.out.println("Xuất báo cáo doanh thu thành công: " + filePath);
                System.out.println("Doanh thu máy tính: " + String.format("%.2f", bookingRevenue));
                System.out.println("Doanh thu đồ ăn/thức uống: " + String.format("%.2f", orderRevenue));
                System.out.println("Tổng doanh thu: " + String.format("%.2f", totalRevenue));
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi xuất báo cáo: " + e.getMessage());
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
