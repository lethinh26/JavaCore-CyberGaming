package ra.cybergaming.model;

import ra.cybergaming.model.enums.OrderStatus;

import java.time.LocalDateTime;

public class OrderStatusHistory {
    private int historyId;
    private int orderId;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private int changed_by;
    private LocalDateTime changedAt;
    private String note;

    public OrderStatusHistory(int historyId, int orderId, OrderStatus oldStatus, OrderStatus newStatus, int changed_by, LocalDateTime changedAt, String note) {
        this.historyId = historyId;
        this.orderId = orderId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changed_by = changed_by;
        this.changedAt = changedAt;
        this.note = note;
    }

    public OrderStatusHistory() {
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(OrderStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public int getChanged_by() {
        return changed_by;
    }

    public void setChanged_by(int changed_by) {
        this.changed_by = changed_by;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
