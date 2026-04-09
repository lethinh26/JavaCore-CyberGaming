package ra.cybergaming.model.enums;

public enum RoleType {
    ADMIN(1, "Admin"),
    STAFF(2, "Staff"),
    CUSTOMER(3, "Customer");

    private final int value;
    private final String displayName;

    RoleType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoleType getByValue(int value) {
        for (RoleType role : RoleType.values()) {
            if (role.value == value) {
                return role;
            }
        }
        return CUSTOMER;
    }
}
