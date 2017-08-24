package com.fei.firstproject.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/29.
 */

public class RoleEntity implements Serializable{


    /**
     * id : 82
     * menuName : 田间管理
     * menuSort : 82
     * menuUrl : 田间管理
     */

    private String id;
    private String menuName;
    private String menuSort;
    private String menuUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuSort() {
        return menuSort;
    }

    public void setMenuSort(String menuSort) {
        this.menuSort = menuSort;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    @Override
    public String toString() {
        return "RoleEntity{" +
                "id='" + id + '\'' +
                ", menuName='" + menuName + '\'' +
                ", menuSort='" + menuSort + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                '}';
    }
}
