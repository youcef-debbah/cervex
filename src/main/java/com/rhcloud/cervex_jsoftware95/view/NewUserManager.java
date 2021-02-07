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
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.beans.UserManager;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.exceptions.EmailExistException;
import com.rhcloud.cervex_jsoftware95.exceptions.UsernameExistException;
import com.rhcloud.cervex_jsoftware95.security.SecurityManager;
import com.rhcloud.cervex_jsoftware95.util.WebKit;
import com.rhcloud.cervex_jsoftware95.validator.Email;
import com.rhcloud.cervex_jsoftware95.validator.EnterpriseAddress;
import com.rhcloud.cervex_jsoftware95.validator.EnterpriseName;
import com.rhcloud.cervex_jsoftware95.validator.Password;
import com.rhcloud.cervex_jsoftware95.validator.PhoneNumber;
import com.rhcloud.cervex_jsoftware95.validator.Username;
import com.rhcloud.cervex_jsoftware95.validator.Website;

/**
 *
 * @author youcef debbah
 */
@Named
@ViewScoped
public class NewUserManager implements LocaleChangeListener, Serializable {
	private static final long serialVersionUID = 6788079356950103987L;

	private static final Logger log = Logger.getLogger(NewUserManager.class);

	private ResourceBundle loginMsgs, userMsgs, generalMsgs;

	@EJB
	private UserManager userManager;

	@NotNull
	@Username
	private String username;
	@NotNull
	@Password
	private String password;
	@NotNull
	private String passwordConfirm;
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
		log.info(NewUserManager.class.getName() + " inisialized");
	}

	public void initMsgs() {
		loginMsgs = MessageBundle.LOGIN.getResource();
		userMsgs = MessageBundle.USER.getResource();
		generalMsgs = MessageBundle.GENERAL.getResource();
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		initMsgs();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getEnterpriseAddress() {
		return enterpriseAddress;
	}

	public void setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String create() {
		FacesContext context = FacesContext.getCurrentInstance();

		String password = getPassword();
		if (password != null && !password.equals(getPasswordConfirm())) {
			context.addMessage("newUserForm:passwordConf",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, userMsgs.getString("passwordNotMatch"), null));
			return null;
		}
		setUsername(getUsername().toLowerCase());

		User newUser = new User();
		newUser.setUsername(getUsername());
		newUser.setPassword(SecurityManager.hashPassword(password));
		newUser.setRole(SecurityManager.USER_ROLE);
		newUser.setPhoneNumber(getPhoneNumber());
		newUser.setEmail(getEmail());
		newUser.setEnterpriseName(getEnterpriseName());
		newUser.setEnterpriseAddress(getEnterpriseAddress());
		newUser.setWebsite(getWebsite());

		try {
			userManager.create(newUser);
		} catch (UsernameExistException e) {
			log.info("username already exist: " + newUser);
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					userMsgs.getString("createUserFailed"), userMsgs.getString("usernameExist")));
			return null;
		} catch (EmailExistException e) {
			log.info("email already exist: " + newUser);
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					userMsgs.getString("createUserFailed"), userMsgs.getString("emailExist")));
			return null;
		} catch (Exception e) {
			log.info("failed to create new user: " + newUser, e);
			createUserFailed(context, newUser);
			return null;
		}

		log.info("user was added to IS: " + newUser);

		HttpServletRequest request = WebKit.getRequest();
		Principal principal = request.getUserPrincipal();

		try {
			if (principal != null) {
				request.logout();
			}

			request.login(getUsername(), getPassword());
			principal = request.getUserPrincipal();
			log.info("succeeded to create user: " + newUser);

			String welcomeBack = MessageFormat.format(loginMsgs.getString("welcome"), principal.getName());
			context.addMessage("global", new FacesMessage(loginMsgs.getString("userConnected"), welcomeBack));
			context.getExternalContext().getFlash().setKeepMessages(true);

			return "userHome";
		} catch (Exception e) {
			log.info("failed to log-in with the new user: " + newUser, e);
			createUserFailed(context, newUser);
			userManager.delete(newUser.getUsername(), true, true);
			return null;
		}
	}

	/**
	 * @param newUser
	 * @param context
	 *
	 */
	private void createUserFailed(FacesContext context, User newUser) {
		context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR,
				userMsgs.getString("createUserFailed"), generalMsgs.getString("internError")));
	}

	@Override
	public String toString() {
		return "NewUserManager [loginMsgs=" + loginMsgs.getBaseBundleName() + ", userMsgs="
				+ userMsgs.getBaseBundleName() + ", generalMsgs=" + generalMsgs.getBaseBundleName() + ", username="
				+ username + "]";
	}

}
