package com.busradeniz.detection.bean;

public class PermissionBean {

    private String permissionName;

    private String permissionInfo;

    private boolean status;

    private String permissionCode;

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionInfo() {
        return permissionInfo;
    }

    public void setPermissionInfo(String permissionInfo) {
        this.permissionInfo = permissionInfo;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
