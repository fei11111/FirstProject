package com.fei.firstproject.entity;

import java.io.Serializable;
import java.util.List;

public class ShareEntity implements Serializable {

    /**
     * "groupId": "003a8afb79d247bab379553b4b41e5e8",
     * "groupName": "其他",
     * "imgPath": "",
     * "value": ";操作内容:覆膜;操作说明:12345678901234567890789456123012345678907894561235654321456865432123653212365980896532147809865321475688332466123456789078906541237894561234789456123512345678907894561236;作物周期:抽蕾开花期",
     * "userName": "武熙斌",
     * "cropName": "马铃薯",
     * "shareUserId": "1118930"
     * “createTime”:””//分享时间
     */

    private String groupId;
    private String groupName;
    private List<ImageEntity> imgPath;
    private String value;
    private String userName;
    private String cropName;
    private String shareUserId;
    private String createTime;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getShareUserId() {
        return shareUserId;
    }

    public void setShareUserId(String shareUserId) {
        this.shareUserId = shareUserId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<ImageEntity> getImgPath() {
        return imgPath;
    }

    public void setImgPath(List<ImageEntity> imgPath) {
        this.imgPath = imgPath;
    }

    public class ImageEntity {

        /**
         * address : 广东省深圳市南山区粤兴二道10
         * path : http://192.168.1.214:8080/btFile///images/nsjl//file42c5c04f-e67d-4e86-9ef8-05dc72a3cb8b
         * time : 2016-09-24 20:58:20
         * nameType : null
         */

        private String address;
        private String path;
        private String time;
        private Object nameType;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Object getNameType() {
            return nameType;
        }

        public void setNameType(Object nameType) {
            this.nameType = nameType;
        }

        @Override
        public String toString() {
            return "ImageEntity{" +
                    "address='" + address + '\'' +
                    ", path='" + path + '\'' +
                    ", time='" + time + '\'' +
                    ", nameType=" + nameType +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ShareBean{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", imgPath=" + imgPath +
                ", value='" + value + '\'' +
                ", userName='" + userName + '\'' +
                ", cropName='" + cropName + '\'' +
                ", shareUserId='" + shareUserId + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
