package ra.cybergaming.model;

import java.time.LocalDateTime;

public class Area {
    private int areaId;
    private String areaName;
    private String areaSize;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Area(int areaId, String areaName, String areaSize, String note, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.areaSize = areaSize;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Area() {
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(String areaSize) {
        this.areaSize = areaSize;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
