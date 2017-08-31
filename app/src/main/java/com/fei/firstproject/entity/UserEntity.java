package com.fei.firstproject.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/7/29.
 */

public class UserEntity implements Serializable {


    /**
     * success : YES
     * returnMsg : 成功
     * img :
     * gender :
     * name : 111920
     * nameString : 黄锦飞
     * mobile : 13652025632
     * id : 1119200
     * roleId : 20,40,63,70
     * page : 1
     * role : [{"list":[{"id":"109","menuName":"农财商城","menuSort":"5","menuUrl":"农财商城"},{"id":"124","menuName":"产品库","menuSort":"86","menuUrl":"产品库"},{"id":"82","menuName":"田间管理","menuSort":"82","menuUrl":"田间管理"},{"id":"83","menuName":"专家诊室","menuSort":"83","menuUrl":"专家诊室"},{"id":"103","menuName":"市场行情","menuSort":"4","menuUrl":"市场行情"},{"id":"87","menuName":"测量","menuSort":"85","menuUrl":"测量"},{"id":"84","menuName":"农业定制","menuSort":"84","menuUrl":"农业定制"}],"name":"业务"}]
     * chatAccount : 111920
     * chatUserId : e54a5d4562074295976830423e47f475
     * inviteCode : 109863
     * QRCode : /images/user/QRCode/1119200.jpg
     * roleName : 种植户 专家 高层 公司职员
     * currentRole : 20
     * userName : 黄锦飞
     */

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
    private List<Role> role;

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

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }

    public static class Role implements Serializable {
        /**
         * list : [{"id":"109","menuName":"农财商城","menuSort":"5","menuUrl":"农财商城"},{"id":"124","menuName":"产品库","menuSort":"86","menuUrl":"产品库"},{"id":"82","menuName":"田间管理","menuSort":"82","menuUrl":"田间管理"},{"id":"83","menuName":"专家诊室","menuSort":"83","menuUrl":"专家诊室"},{"id":"103","menuName":"市场行情","menuSort":"4","menuUrl":"市场行情"},{"id":"87","menuName":"测量","menuSort":"85","menuUrl":"测量"},{"id":"84","menuName":"农业定制","menuSort":"84","menuUrl":"农业定制"}]
         * name : 业务
         */

        private String name;
        private List<RoleMenuEntity> list;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<RoleMenuEntity> getList() {
            return list;
        }

        public void setList(List<RoleMenuEntity> list) {
            this.list = list;
        }

    }
}
