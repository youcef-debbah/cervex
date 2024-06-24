package com.rhcloud.cervex_jsoftware95.control;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public enum MessageBundle {

	GENERAL("general"), HOME("home"), ERP("erp"), APPLICATIONS("applications"), TRAINING("training"),

	LOGIN("login"), ABOUT_US("aboutUs"), USER("userMsg"), ADMIN("adminMsg"), ARTICLE("article"),

	MESSAGING("messaging"), IMAGES("images"), BLOG("blog");

	private static final String EL_PREFIX = "#";
	private static final String EL_FORMAT = EL_PREFIX + "{%s}";
	private String el;

	private MessageBundle(String resourceBundle) {
		this.el = String.format(EL_FORMAT, resourceBundle);
	}

	public ResourceBundle getResource() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			return context.getApplication().evaluateExpressionGet(context, el, ResourceBundle.class);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return el;
	}
}
