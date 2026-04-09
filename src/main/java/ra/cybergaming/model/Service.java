package ra.cybergaming.model;

import ra.cybergaming.model.enums.CategoryType;
import ra.cybergaming.model.enums.ServiceStatus;

import java.time.LocalDateTime;

public class Service {
    private int serviceId;
    private String serviceCode;
    private String serviceName;
    private CategoryType category;
    private String description;
    private double price;
    private int stock_quantity;
    private ServiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Service(int serviceId, String serviceCode, String serviceName, CategoryType category, String description, double price, int stock_quantity, ServiceStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.serviceId = serviceId;
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock_quantity = stock_quantity;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Service() {
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
