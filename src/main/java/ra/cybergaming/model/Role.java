package ra.cybergaming.model;

public class Role {
    private int roleId;
    private int roleName;


    public Role(int roleId, int roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }


    public Role() {
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleName() {
        return roleName;
    }

    public void setRoleName(int roleName) {
        this.roleName = roleName;
    }
}
