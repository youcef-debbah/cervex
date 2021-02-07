package com.rhcloud.cervex_jsoftware95.exceptions;

public class EmailExistException extends InformationSystemException {

	private static final long serialVersionUID = 1139006020296505792L;

	private String email;

	public EmailExistException(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
