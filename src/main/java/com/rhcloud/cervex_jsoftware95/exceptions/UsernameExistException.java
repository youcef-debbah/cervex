package com.rhcloud.cervex_jsoftware95.exceptions;

public class UsernameExistException extends InformationSystemException {

	private static final long serialVersionUID = -3710891710301059878L;

	private String username;

	public UsernameExistException(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
