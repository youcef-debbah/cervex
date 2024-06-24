/*
 * Copyright (c) 2018 youcef debbah (youcef-kun@hotmail.fr)
 *
 * This file is part of cervex.
 *
 * cervex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cervex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cervex.  If not, see <http://www.gnu.org/licenses/>.
 *
 * created on 2018/12/08
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.beans.UserManager;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.validator.Email;
import com.rhcloud.cervex_jsoftware95.validator.EnterpriseAddress;
import com.rhcloud.cervex_jsoftware95.validator.EnterpriseName;
import com.rhcloud.cervex_jsoftware95.validator.PhoneNumber;
import com.rhcloud.cervex_jsoftware95.validator.Website;

/**
 *
 * @author youcef debbah
 */
@Named
@ViewScoped
public class ProfileManager implements LocaleChangeListener, Serializable {

	private static final long serialVersionUID = -8688579261475951773L;

	private static Logger log = Logger.getLogger(ProfileManager.class);

	private final String USER_MSG = "#{userMsg}";
	private final String GENERAL_MSG = "#{general}";

	private ResourceBundle msgs;
	private ResourceBundle generalMsgs;

	@EJB
	private UserManager userManager;

	@NotNull
	@PhoneNumber
	private String phoneNumber;

	@NotNull
	@Email
	private String email;

	@EnterpriseName
	private String enterpriseName;

	@EnterpriseAddress
	private String enterpriseAddress;

	@Website
	private String website;

	@PostConstruct
	public void init() {
		initMsgs();

		try {
			User user = userManager.getCurrentUser();
			setPhoneNumber(user.getPhoneNumber());
			setEmail(user.getEmail());
			setEnterpriseName(user.getEnterpriseName());
			setEnterpriseAddress(user.getEnterpriseAddress());
			setWebsite(user.getWebsite());
		} catch (Exception e) {
			Meta.handleInternalError(e);
		}
	}

	public void initMsgs() {
		FacesContext context = FacesContext.getCurrentInstance();
		msgs = context.getApplication().evaluateExpressionGet(context, USER_MSG, ResourceBundle.class);
		log.info("Resource Bundle successfully loaded: " + msgs.getBaseBundleName());

		generalMsgs = context.getApplication().evaluateExpressionGet(context, GENERAL_MSG, ResourceBundle.class);
		log.info("Resource Bundle successfully loaded: " + generalMsgs.getBaseBundleName());
	}

	private void updateUser(User currentUser, String successMsg, Runnable cleaner) {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			userManager.updateUser(currentUser);
			context.addMessage("global",
					new FacesMessage(msgs.getString(successMsg), generalMsgs.getString("changesSaved")));
		} catch (Exception e) {
			log.error("intern error while updating user: " + currentUser, e);
			if (cleaner != null)
				cleaner.run();
			Meta.handleInternalError(e);
		}
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		initMsgs();
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void updatePhoneNumber() {
		User currentUser;
		try {
			currentUser = userManager.getCurrentUser();
		} catch (Exception e) {
			Meta.handleInternalError(e);
			return;
		}
		String oldPhoneNumber = currentUser.getPhoneNumber();
		currentUser.setPhoneNumber(getPhoneNumber());
		updateUser(currentUser, "phoneNumberUpdateDone", () -> setPhoneNumber(oldPhoneNumber));
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void updateEmail() {
		User currentUser;
		try {
			currentUser = userManager.getCurrentUser();
		} catch (Exception e) {
			Meta.handleInternalError(e);
			return;
		}
		String oldEmail = currentUser.getEmail();
		currentUser.setEmail(getEmail());
		updateUser(currentUser, "emailUpdateDone", () -> setEmail(oldEmail));
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public void updateEnterpriseName() {
		User currentUser;
		try {
			currentUser = userManager.getCurrentUser();
		} catch (Exception e) {
			Meta.handleInternalError(e);
			return;
		}
		String oldEnterpriseName = currentUser.getEnterpriseName();
		currentUser.setEnterpriseName(getEnterpriseName());
		updateUser(currentUser, "enterpriseNameUpdateDone", () -> setEnterpriseName(oldEnterpriseName));
	}

	public String getEnterpriseAddress() {
		return enterpriseAddress;
	}

	public void setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
	}

	public void updateEnterpriseAddress() {
		User currentUser;
		try {
			currentUser = userManager.getCurrentUser();
		} catch (Exception e) {
			Meta.handleInternalError(e);
			return;
		}
		String oldEnterpriseAddress = currentUser.getEnterpriseAddress();
		currentUser.setEnterpriseAddress(getEnterpriseAddress());
		updateUser(currentUser, "enterpriseAddressUpdateDone", () -> setEnterpriseAddress(oldEnterpriseAddress));
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public void updateWebsite() {
		User currentUser;
		try {
			currentUser = userManager.getCurrentUser();
		} catch (Exception e) {
			Meta.handleInternalError(e);
			return;
		}
		String oldWebsite = currentUser.getWebsite();
		currentUser.setWebsite(getWebsite());
		updateUser(currentUser, "websiteUpdateDone", () -> setWebsite(oldWebsite));
	}

	@Override
	public String toString() {
		return "ProfileManager [msgs=" + (msgs != null ? msgs.getBaseBundleName() : msgs) + ", phoneNumber="
				+ phoneNumber + ", email=" + email + ", enterpriseName=" + enterpriseName + ", enterpriseAddress="
				+ enterpriseAddress + "]";
	}

}
