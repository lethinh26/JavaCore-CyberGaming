package ra.cybergaming.model;

import ra.cybergaming.model.enums.WorkingStationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Workstation {
    private int workstationId;
    private String stationCode;
    private String stationName;
    private int areaId;
    private String specification;
    private double hourlyRate;
    private WorkingStationStatus status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Workstation(int workstationId, String stationCode, String stationName, int areaId, String specification, double hourlyRate, WorkingStationStatus status, String note, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.workstationId = workstationId;
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.areaId = areaId;
        this.specification = specification;
        this.hourlyRate = hourlyRate;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Workstation() {
    }

    public int getWorkstationId() {
        return workstationId;
    }

    public void setWorkstationId(int workstationId) {
        this.workstationId = workstationId;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public WorkingStationStatus getStatus() {
        return status;
    }

    public void setStatus(WorkingStationStatus status) {
        this.status = status;
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
