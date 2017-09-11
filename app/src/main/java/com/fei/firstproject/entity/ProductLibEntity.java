package com.fei.firstproject.entity;

/**
 * Author：Administrator
 * Date：2017/6/9 10:51
 * Description：
 */
public class ProductLibEntity {


    /**
     * imagePath :
     * label :
     * matieralName : 尿素
     * matieralId : 1101010001
     */

    private String imagePath;
    private String label;
    private String matieralName;
    private String matieralId;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMatieralName() {
        return matieralName;
    }

    public void setMatieralName(String matieralName) {
        this.matieralName = matieralName;
    }

    public String getMatieralId() {
        return matieralId;
    }

    public void setMatieralId(String matieralId) {
        this.matieralId = matieralId;
    }
}
