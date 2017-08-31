package com.fei.firstproject.entity;

import java.util.List;

/**
 * Created by Fei on 2017/8/31.
 */

public class SelfInfoEntity {

    private String createTime;
    private String userDesc;
    private String phone;
    private String jpgPath;
    private String userName;
    private String roleName;
    private List<RoleEntity> roleList;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJpgPath() {
        return jpgPath;
    }

    public void setJpgPath(String jpgPath) {
        this.jpgPath = jpgPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<RoleEntity> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleEntity> roleList) {
        this.roleList = roleList;
    }
}
