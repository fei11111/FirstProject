package com.fei.firstproject.entity;

/**
 * Created by Administrator on 2017/9/4.
 */

public class ChangeRoleEntity {

    private String ID;
    private String ROLE_NAME;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getROLE_NAME() {
        return ROLE_NAME;
    }

    public void setROLE_NAME(String ROLE_NAME) {
        this.ROLE_NAME = ROLE_NAME;
    }

    @Override
    public String toString() {
        return "ChangeRole{" +
                "ID='" + ID + '\'' +
                ", ROLE_NAME='" + ROLE_NAME + '\'' +
                '}';
    }
}
