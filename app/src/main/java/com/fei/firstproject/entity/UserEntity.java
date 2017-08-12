package com.fei.firstproject.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/7/29.
 */

public class UserEntity {
    private String success;
    private String returnMsg;
    private String img;
    private String gender;
    private String name;
    private String nameString;
    private String mobile;
    private String id;
    private String roleId;
    private String page;
    private String chatAccount;
    private String chatUserId;
    private String inviteCode;
    private String QRCode;
    private String roleName;
    private String currentRole;
    private String userName;
    private List<RoleEntity> roles;
    private String token;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameString() {
        return nameString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getChatAccount() {
        return chatAccount;
    }

    public void setChatAccount(String chatAccount) {
        this.chatAccount = chatAccount;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "success='" + success + '\'' +
                ", returnMsg='" + returnMsg + '\'' +
                ", img='" + img + '\'' +
                ", gender='" + gender + '\'' +
                ", name='" + name + '\'' +
                ", nameString='" + nameString + '\'' +
                ", mobile='" + mobile + '\'' +
                ", id='" + id + '\'' +
                ", roleId='" + roleId + '\'' +
                ", page='" + page + '\'' +
                ", chatAccount='" + chatAccount + '\'' +
                ", chatUserId='" + chatUserId + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", QRCode='" + QRCode + '\'' +
                ", roleName='" + roleName + '\'' +
                ", currentRole='" + currentRole + '\'' +
                ", userName='" + userName + '\'' +
                ", roles=" + roles +
                ", token='" + token + '\'' +
                '}';
    }
}
