package com.fei.firstproject.entity;

import java.io.Serializable;

/**
 * @author huangjf
 * @version 创建时间：2016-4-27 下午2:38:17
 * @description 问题
 */
public class HotQuestionEntity implements Serializable {
	
	/**
	"attention": true,
	"jpgPath": "",
	"expertId": "444",
	"expertName": "张三",
	"tel": "",
	"serviceTime": "3",
	"levelStart": 5,
	"expertise": "栽培管理,科学施肥",
	"plantList": "茄子,西瓜",
	"chatAccount": "11223",
	"answerHeadId": "e8b053fb0bfd47bc94b0b7fff46b8fba",
	"userName": "侯晓平",
	"question": "test",
	"expertorConfTime": "2016-05-13",
	"standardAnswer": "<p style=\"text-align: center;\">16:10:01<strong>多少钱啊的<\/strong><strong><span style=\"text-decoration: underline;\">阿达是阿萨德按时阿达<\/span><\/strong><\/p><p><strong><span style=\"text-decoration: underline;\">

	 * */
	
	private String jpgPath;
	private String expertId;
	private String expertDesc;
	private String expertName;
	private String serviceTime;
	private float levelStart;
	private String expertise;
	private String plantList;

	// 问题id
	private String answerHeadId;
	// 提问者我
	private String userName;
	// 标题
	private String question;
	// 专家id
	private String expertorId;
	// 时间
	private String expertorConfTime;

	private String standardAnswer;
	
	private String chatUserId;
	
	private boolean attention;
	
	private int commentCount;
	
	private String tel;
	
	private String consultType;
	
	private boolean online;

	public String getJpgPath() {
		return jpgPath;
	}

	public void setJpgPath(String jpgPath) {
		this.jpgPath = jpgPath;
	}

	public String getExpertId() {
		return expertId;
	}

	public void setExpertId(String expertId) {
		this.expertId = expertId;
	}

	public String getExpertDesc() {
		return expertDesc;
	}

	public void setExpertDesc(String expertDesc) {
		this.expertDesc = expertDesc;
	}

	public String getExpertName() {
		return expertName;
	}

	public void setExpertName(String expertName) {
		this.expertName = expertName;
	}

	public String getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(String serviceTime) {
		this.serviceTime = serviceTime;
	}

	public float getLevelStart() {
		return levelStart;
	}

	public void setLevelStart(float levelStart) {
		this.levelStart = levelStart;
	}

	public String getExpertise() {
		return expertise;
	}

	public void setExpertise(String expertise) {
		this.expertise = expertise;
	}

	public String getPlantList() {
		return plantList;
	}

	public void setPlantList(String plantList) {
		this.plantList = plantList;
	}

	public String getAnswerHeadId() {
		return answerHeadId;
	}

	public void setAnswerHeadId(String answerHeadId) {
		this.answerHeadId = answerHeadId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getExpertorId() {
		return expertorId;
	}

	public void setExpertorId(String expertorId) {
		this.expertorId = expertorId;
	}

	public String getExpertorConfTime() {
		return expertorConfTime;
	}

	public void setExpertorConfTime(String expertorConfTime) {
		this.expertorConfTime = expertorConfTime;
	}

	public String getStandardAnswer() {
		return standardAnswer;
	}

	public void setStandardAnswer(String standardAnswer) {
		this.standardAnswer = standardAnswer;
	}
	public boolean isAttention() {
		return attention;
	}

	public void setAttention(boolean attention) {
		this.attention = attention;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getChatUserId() {
		return chatUserId;
	}

	public void setChatUserId(String chatUserId) {
		this.chatUserId = chatUserId;
	}

	public String getConsultType() {
		return consultType;
	}

	public void setConsultType(String consultType) {
		this.consultType = consultType;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public String toString() {
		return "HotQuestionBean [jpgPath=" + jpgPath + ", expertId=" + expertId
				+ ", expertDesc=" + expertDesc + ", expertName=" + expertName
				+ ", serviceTime=" + serviceTime + ", levelStart=" + levelStart
				+ ", expertise=" + expertise + ", plantList=" + plantList
				+ ", answerHeadId=" + answerHeadId + ", userName=" + userName
				+ ", question=" + question + ", expertorId=" + expertorId
				+ ", expertorConfTime=" + expertorConfTime
				+ ", standardAnswer=" + standardAnswer + ", chatUserId="
				+ chatUserId + ", attention=" + attention + ", commentCount="
				+ commentCount + ", tel=" + tel + ", consultType="
				+ consultType + ", online=" + online + "]";
	}

}
