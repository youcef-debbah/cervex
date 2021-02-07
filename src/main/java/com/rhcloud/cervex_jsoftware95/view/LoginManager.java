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
import java.util.Objects;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.security.SecurityManager;
import com.rhcloud.cervex_jsoftware95.util.WebKit;
import com.rhcloud.cervex_jsoftware95.validator.Password;
import com.rhcloud.cervex_jsoftware95.validator.Username;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class LoginManager implements LocaleChangeListener, Serializable {
	private static final long serialVersionUID = -1791839482568436535L;

	private static final Logger log = Logger.getLogger(LoginManager.class);

	private ResourceBundle msgs;

	@Username
	private String username;
	@Password
	private String password;

	private String nextView;
	private String isNewView;
	private String originalURL;

	@PostConstruct
	public void init() {
		initMsgs();
		originalURL = WebKit.getOriginalURL(FacesContext.getCurrentInstance(), false);
		log.info(LoginManager.class.getName() + " initialized with originalURL: " + originalURL + ", and next view: "
				+ nextView);
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		initMsgs();
	}

	private void initMsgs() {
		msgs = MessageBundle.LOGIN.getResource();
	}

	public String login() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = WebKit.getRequest();
		Principal principal = request.getUserPrincipal();
		setUsername(getUsername().toLowerCase());

		try {
			if (principal == null) {
				request.login(getUsername(), getPassword());
				principal = request.getUserPrincipal();
				log.info("succeeded to authenticate user: " + principal.getName());
			} else {
				log.info("user is already loged-in: " + principal);
			}

			String welcomeBack = MessageFormat.format(msgs.getString("welcomeBack"), principal.getName());
			context.addMessage("global", new FacesMessage(msgs.getString("userConnected"), welcomeBack));
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getFlash().setKeepMessages(true);

			String path = externalContext.getRequestContextPath();

			if (nextView != null) {
				if (principalHasPermission(request, nextView)) {
					String url = path + nextView;
					if (getIsNewView() != null) {
						String separator = nextView.contains("?") ? "&" : "?";
						url += separator + "isNewView=true";
					}
					log.info("redirecting to nextView: " + url);
					externalContext.redirect(url);
					return null;
				} else {
					return "forbidden";
				}
			}

			if (originalURL != null) {
				if (principalHasPermission(request, originalURL)) {
					log.info("redirecting to originalURL: " + originalURL);
					externalContext.redirect(path + originalURL);
					return null;
				} else {
					return "forbidden";
				}
			}

			if (request.isUserInRole(SecurityManager.ADMIN_ROLE)) {
				return "adminHome";
			} else {
				return "userHome";
			}

		} catch (Exception e) {
			log.info("failed to authenticate user: " + username, e);
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR, msgs.getString("loginFailed"),
					msgs.getString("loginFailedDetail")));
			return null;
		}
	}

	/**
	 * @param principal
	 * @param originalURL2
	 * @return
	 */
	private boolean principalHasPermission(HttpServletRequest request, String url) {
		Objects.requireNonNull(request);

		if (url == null) {
			return true;
		} else {
			if (url.startsWith(SecurityManager.ADMIN_DOMAIN)) {
				return request.isUserInRole(SecurityManager.ADMIN_ROLE);
			} else if (url.startsWith(SecurityManager.USER_DOMAIN)) {
				return request.isUserInRole(SecurityManager.USER_ROLE);
			} else if (url.startsWith(SecurityManager.SECURED_DOMAIN)) {
				return request.isUserInRole(SecurityManager.ADMIN_ROLE)
						|| request.isUserInRole(SecurityManager.USER_ROLE);
			} else {
				return true;
			}
		}
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

	public String getNextView() {
		return nextView;
	}

	public void setNextView(String nextView) {
		if ("".equals(nextView) || "null".equals(nextView)) {
			this.nextView = null;
		} else {
			this.nextView = nextView;
		}
	}

	public String getIsNewView() {
		return isNewView;
	}

	public void setIsNewView(String isNewView) {
		this.isNewView = isNewView;
	}

	@Override
	public String toString() {
		return "LoginManager [msgs=" + (msgs != null ? msgs.getBaseBundleName() : msgs) + ", username=" + username
				+ ", password=" + password + "]";
	}

}
