package com.fei.firstproject.entity;

/**
 * Created by Administrator on 2017/8/14.
 */

public class RecommendEntity {


    /**
     * id : 00e9749aae46482cbb875305be4783b6
     * content :
     * title : 海南陵水县樱桃番茄种植解决方案
     * imgPath : http://192.168.1.214:3391/btFile//images/plant/JPG7f88474d-1227-4c18-a245-225cb90e17a2.png
     * cropCategoryCode : 200
     * cropCode : 20020
     * version : 1
     */

    private String id;
    private String content;
    private String title;
    private String imgPath;
    private String cropCategoryCode;
    private String cropCode;
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getCropCategoryCode() {
        return cropCategoryCode;
    }

    public void setCropCategoryCode(String cropCategoryCode) {
        this.cropCategoryCode = cropCategoryCode;
    }

    public String getCropCode() {
        return cropCode;
    }

    public void setCropCode(String cropCode) {
        this.cropCode = cropCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "RecommendEntity{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", cropCategoryCode='" + cropCategoryCode + '\'' +
                ", cropCode='" + cropCode + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
