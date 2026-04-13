package ra.cybergaming.model;

public class Area {
    private int areaId;
    private String areaName;
    private String description;

    public Area(int areaId, String areaName, String description) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
