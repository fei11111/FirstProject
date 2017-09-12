package com.fei.firstproject.entity;

import java.io.Serializable;

/**
 * @author huangjf
 * @version 创建时间：2016-5-4 上午10:10:51
 * @description
 */
public class UnSolveQuestionEntity implements Serializable {

	/*
	 * 
	 * "userId": "111143",
	"userName": "侯晓平",
	"expertorId": "1",
	"expertor": "张三",
	"chatAccount": "",
	"communicationId": "121212121",
	"questionId": "3",
	"question": "荔枝种植初期的病虫防治",
	"questionTime": "2016-05-03"
	 * 
	 * */
	
	private String userId;
	private String userName;
	private String expertorId;
	private String chatUserId;
	private String questionId;
	private String question;
	private String questionTime;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getExpertorId() {
		return expertorId;
	}
	public void setExpertorId(String expertorId) {
		this.expertorId = expertorId;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getQuestionTime() {
		return questionTime;
	}
	public void setQuestionTime(String questionTime) {
		this.questionTime = questionTime;
	}
	public String getChatUserId() {
		return chatUserId;
	}
	public void setChatUserId(String chatUserId) {
		this.chatUserId = chatUserId;
	}
	@Override
	public String toString() {
		return "UnSolveQuestionBean [userId=" + userId + ", userName="
				+ userName + ", expertorId=" + expertorId + ", chatUserId="
				+ chatUserId + ", questionId=" + questionId + ", question="
				+ question + ", questionTime=" + questionTime + "]";
	}
	
	
}
