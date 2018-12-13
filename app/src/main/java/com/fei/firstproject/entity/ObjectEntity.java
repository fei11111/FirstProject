package com.fei.firstproject.entity;

import java.util.List;

public class ObjectEntity {

    private String name;
    private List<ObjectEntity> objects;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjectEntity> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectEntity> objects) {
        this.objects = objects;
    }
}
