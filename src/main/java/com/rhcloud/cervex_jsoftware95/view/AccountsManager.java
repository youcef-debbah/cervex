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
 * created on 2018/01/06
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.rhcloud.cervex_jsoftware95.beans.UserManager;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.control.OperationOutput;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.security.SecurityManager;
import com.rhcloud.cervex_jsoftware95.util.WebKit;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class AccountsManager implements LocaleChangeListener, Serializable {

	private static final long serialVersionUID = -7529359910608973878L;

	private static final Logger log = Logger.getLogger(AccountsManager.class);

	private ResourceBundle messagingMsgs;
	private ResourceBundle userMsgs;

	@EJB
	UserManager userManager;

	private User selectedAccount;
	private List<User> accounts;

	@PostConstruct
	public void init() {
		messagingMsgs = MessageBundle.MESSAGING.getResource();
		userMsgs = MessageBundle.USER.getResource();
		log.info(AccountsManager.class.getName() + " initialized");
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		init();
	}

	public List<User> getAccounts() {
		if (accounts == null) {
			update();
		}

		return accounts;
	}

	public User getSelectedAccount() {
		return selectedAccount;
	}

	public void setSelectedAccount(User selectedAccount) {
		this.selectedAccount = selectedAccount;
	}

	private void update() {
		try {
			List<User> allUsers = userManager.getAllUsers();
			List<User> accounts = new ArrayList<>();

			for (User user : allUsers) {
				if (user.getRole().equals(SecurityManager.USER_ROLE)) {
					accounts.add(user);
				}
			}

			this.accounts = accounts;
		} catch (Exception e) {
			accounts = new ArrayList<>();
			Meta.handleInternalError(e);
		}
		updateSelection();
	}

	private void updateSelection() {
		if (selectedAccount != null) {
			String id = selectedAccount.getUserID();
			List<User> accounts = getAccounts();
			selectedAccount = null;

			for (User account : accounts) {
				if (account.getUserID().equals(id)) {
					selectedAccount = account;
					return;
				}
			}
		}
	}

	public void openDeleteAccountDialog() {
		Map<String, Object> options = new HashMap<>();
		options.put("modal", true);
		options.put("width", 530);
		options.put("height", 180);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		options.put("headerElement", userMsgs.getString("deleteUser"));

		Map<String, List<String>> params = new HashMap<>();
		User user = getSelectedAccount();
		if (user != null) {
			params.put("account", Arrays.asList(user.getUsername()));
		}

		RequestContext.getCurrentInstance().openDialog("deleteAccountDialog", options, params);
	}

	public void onUserDelete(SelectEvent event) {
		Object result = event.getObject();
		if (result != null) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = null;

			if (result == OperationOutput.SUCCESS) {
				message = new FacesMessage(userMsgs.getString("userDeleted"), userMsgs.getString("userDeletedInfo"));
				update();
			} else if (result == OperationOutput.FAILED) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, userMsgs.getString("cannotDeleteUser"),
						userMsgs.getString("cannotDeleteUserInfo"));
			}

			if (message != null)
				context.addMessage("global", message);
		}
	}

	public void newMessage() {
		String receiver = getSelectedAccount().getUsername();
		log.info("sending message to: " + receiver);

		if (receiver != null) {
			String sender = WebKit.getRequest().getUserPrincipal().getName();

			Map<String, List<String>> params = new HashMap<>();
			params.put(MessagingManager.SENDER_PARAMETER, Arrays.asList(sender));
			params.put(MessagingManager.RECEIVER_PARAMETER, Arrays.asList(receiver));

			InboxManager.sendMessage(params);
		}
	}

	public void onMessageSend(SelectEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();

		if (event.getObject() != null) {
			context.addMessage("global", new FacesMessage(messagingMsgs.getString("messageSent"),
					messagingMsgs.getString("messageSentDetail")));
			update();
		} else {
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					messagingMsgs.getString("messageSentFailed"), messagingMsgs.getString("messageSentFailedDetail")));
		}
	}

}
