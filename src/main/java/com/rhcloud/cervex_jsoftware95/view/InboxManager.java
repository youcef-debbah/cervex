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
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.rhcloud.cervex_jsoftware95.beans.MessageManager;
import com.rhcloud.cervex_jsoftware95.beans.UserManager;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.control.NotificationRemovedListener;
import com.rhcloud.cervex_jsoftware95.entities.Message;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.security.SecurityManager;
import com.rhcloud.cervex_jsoftware95.util.WebKit;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class InboxManager implements LocaleChangeListener, NotificationRemovedListener, Serializable {

	private static final long serialVersionUID = 2431816372466605822L;

	private static final Logger log = Logger.getLogger(InboxManager.class);

	private ResourceBundle msgs;

	@EJB
	private UserManager userManager;

	@EJB
	private MessageManager messageManager;

	private Message selectedMessage;
	private List<Message> messages;
	private boolean onlyNew;

	@PostConstruct
	public void init() {
		msgs = MessageBundle.MESSAGING.getResource();
		log.info(InboxManager.class.getName() + " initialized");
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		init();
	}

	@Override
	public void onNotificationRemoved() {
		update();
	}

	private void markMessageAsRead(String messageID) {
		if (messageID != null) {
			try {
				messageManager.markAsRead(messageID);
				Meta.broadcastOverViewScop(NotificationRemovedListener.class,
						NotificationRemovedListener::onNotificationRemoved);
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}

	public void setSelectedMessage(Message selectedMessage) {
		this.selectedMessage = selectedMessage;
		if (selectedMessage != null && selectedMessage.isNew()) {
			markMessageAsRead(selectedMessage.getMessageID());
		}
	}

	public void onMessageToggle() {
		String messageID = WebKit.getRequest().getParameter("id");
		if (messageID != null) {
			markMessageAsRead(messageID);
		}
	}

	public Message getSelectedMessage() {
		return selectedMessage;
	}

	public List<Message> getMessages() {
		if (messages == null) {
			update();
		}

		return messages;
	}

	private void update() {
		try {
			messages = messageManager.getMessagesFor(userManager.getCurrentUser().getUserID());
			if (isOnlyNew()) {
				messages = messages.stream().filter(Message::isNew).collect(Collectors.toList());
			}
		} catch (Exception e) {
			messages = new ArrayList<>();
			Meta.handleInternalError(e);
		}
		updateSelection();
	}

	private void updateSelection() {
		if (selectedMessage != null) {
			String id = selectedMessage.getMessageID();
			List<Message> list = getMessages();

			selectedMessage = null;
			for (Message message : list) {
				if (message.getMessageID().equals(id)) {
					selectedMessage = message;
					return;
				}
			}
		}
	}

	public static void sendMessage(Map<String, List<String>> params) {
		Map<String, Object> options = new HashMap<>();
		options.put("modal", true);
		options.put("width", 800);
		options.put("height", 460);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		options.put("maximizable", true);
		options.put("headerElement", "customheader");

		RequestContext.getCurrentInstance().openDialog("sendMessage", options, params);
	}

	public void reply() {
		if (selectedMessage != null) {
			Map<String, List<String>> params = new HashMap<>();
			params.put(MessagingManager.SENDER_PARAMETER,
					Arrays.asList(WebKit.getRequest().getUserPrincipal().getName()));
			params.put(MessagingManager.RECEIVER_PARAMETER, Arrays.asList(selectedMessage.getSender().getUsername()));
			sendMessage(params);
		}
	}

	public void onMessageSend(SelectEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();

		if (event.getObject() != null) {
			context.addMessage("global",
					new FacesMessage(msgs.getString("messageSent"), msgs.getString("messageSentDetail")));
			update();
		} else {
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					msgs.getString("messageSentFailed"), msgs.getString("messageSentFailedDetail")));
		}
	}

	public void onOnlyNewClick(AjaxBehaviorEvent event) {
		update();
	}

	public void newMessage() {
		String sender = WebKit.getRequest().getUserPrincipal().getName();

		Map<String, List<String>> params = new HashMap<>();
		params.put(MessagingManager.SENDER_PARAMETER, Arrays.asList(sender));

		List<User> users;
		try {
			users = userManager.getAllUsers();
		} catch (Exception e) {
			users = null;
			Meta.handleInternalError(e);
		}

		List<String> receivers = new ArrayList<>();
		if (users != null) {
			if (WebKit.getRequest().isUserInRole(SecurityManager.ADMIN_ROLE)) {
				for (User user : users) {
					if (!sender.equals(user.getUsername())) {
						receivers.add(user.getUsername());
					}
				}
			} else {
				for (User user : users) {
					if (!sender.equals(user.getUsername()) && user.getRole().equals(SecurityManager.ADMIN_ROLE)) {
						receivers.add(user.getUsername());
					}
				}
			}
		}

		params.put(MessagingManager.RECEIVERS_LIST_PARAMETER, receivers);
		sendMessage(params);
	}

	public boolean isOnlyNew() {
		return onlyNew;
	}

	public void setOnlyNew(boolean onlyNew) {
		this.onlyNew = onlyNew;
	}

}
