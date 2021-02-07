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
 * created on 2018/01/07
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.beans.ArticleManager;
import com.rhcloud.cervex_jsoftware95.beans.BlogManager;
import com.rhcloud.cervex_jsoftware95.beans.MessageManager;
import com.rhcloud.cervex_jsoftware95.beans.NotificationManager;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.control.NotificationRemovedListener;
import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.Comment;
import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.entities.Message;
import com.rhcloud.cervex_jsoftware95.security.SecurityManager;
import com.rhcloud.cervex_jsoftware95.util.WebKit;

/**
 *
 * @author youcef debbah
 */
@Named
@ViewScoped
public class NotificationView implements LocaleChangeListener, NotificationRemovedListener, Serializable {

	private static final long serialVersionUID = -556693690613202273L;

	private static final Logger log = Logger.getLogger(NotificationView.class);

	private List<Demand> newDemands;
	
	private List<Comment> newComments;

	private List<Article> newArticles;

	private List<Article> updatedArticles;

	private List<Message> newReceivedMessages;

	private List<Notification> notifications;

	private ResourceBundle articleMsgs;
	private ResourceBundle messagingMsgs;
	private ResourceBundle blogMsgs;

	@EJB
	private NotificationManager notificationManager;

	@EJB
	private ArticleManager am;

	@EJB
	private MessageManager mm;
	
	@EJB
	private BlogManager bm;

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		initMsgs();
	}

	@Override
	public void onNotificationRemoved() {
		update();
	}

	private void initMsgs() {
		articleMsgs = MessageBundle.ARTICLE.getResource();
		messagingMsgs = MessageBundle.MESSAGING.getResource();
		blogMsgs = MessageBundle.BLOG.getResource();
	}

	@PostConstruct
	public void init() {
		initMsgs();
		update();
	}

	private void update() {
		HttpServletRequest request = WebKit.getRequest();
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			String username = principal.getName();
			newReceivedMessages = notificationManager.getNewReceivedMessages(username);
			if (request.isUserInRole(SecurityManager.ADMIN_ROLE)) {
				newDemands = notificationManager.getNewDemands();
				newComments = notificationManager.getNewComments();
			} else if (request.isUserInRole(SecurityManager.USER_ROLE)) {
				newArticles = notificationManager.getNewArticles(username);
				updatedArticles = notificationManager.getUpdatedArticles(username);
			}
		}

		initNotification();
	}

	private void initNotification() {
		notifications = new ArrayList<>();

		Runnable updater = () -> Meta.broadcastOverViewScop(NotificationRemovedListener.class,
				NotificationRemovedListener::onNotificationRemoved);

		Supplier<String> goToArticles = () -> "business";

		if (newArticles != null) {
			for (Article article : newArticles) {
				Notification notification = new Notification(articleMsgs.getString("newArticle"));
				notification.setContents(articleMsgs.getString("articleCode") + ": " + article.getArticleID());
				notification.setIcon("fa-cart-plus");
				notification.setDate(article.getCreationDate());
				notification.setRemover(() -> am.markAsOldArticle(article.getArticleID()));
				notification.setOutcomeSupplier(goToArticles);
				notification.setUpdater(updater);
				notifications.add(notification);
			}
		}

		if (updatedArticles != null) {
			for (Article article : updatedArticles) {
				Notification notification = new Notification(articleMsgs.getString("updatedArticle"));
				notification.setContents(articleMsgs.getString("articleCode") + ": " + article.getArticleID());
				notification.setIcon("fa-arrow-circle-up");
				Date updateDate = article.getLastUpdate();
				if (updateDate == null) {
					updateDate = article.getCreationDate();
				}
				notification.setDate(updateDate);
				notification.setRemover(() -> am.markAsOldArticle(article.getArticleID()));
				notification.setOutcomeSupplier(goToArticles);
				notification.setUpdater(updater);
				notifications.add(notification);
			}
		}

		if (newDemands != null) {
			Supplier<String> goToNewDemands = () -> "applies";

			for (Demand demand : newDemands) {
				Notification notification = new Notification(articleMsgs.getString("newDemandReceived"));
				notification.setContents(articleMsgs.getString("demandTitle") + ": " + demand.getTitle());
				notification.setIcon("fa-cart-plus");
				notification.setDate(demand.getDemandDate());
				notification.setRemover(() -> am.markAsOldDemand(demand.getDemandID()));
				notification.setOutcomeSupplier(goToNewDemands);
				notification.setUpdater(updater);
				notifications.add(notification);
			}
		}
		
		if (newComments != null) {
			for (Comment comment : newComments) {
				Notification notification = new Notification(blogMsgs.getString("newComment"));
				notification.setContents(blogMsgs.getString("poster") + ": " + comment.getBlog().getTitle());
				notification.setIcon("fa-edit");
				notification.setDate(comment.getDate());
				notification.setRemover(() -> bm.authorizeComment(comment.getCommentID()));
				notification.setOutcomeSupplier(() -> "newComments");
				notification.setUpdater(updater);
				notifications.add(notification);
			}
		}

		if (newReceivedMessages != null) {
			Supplier<String> goToNewMessages = () -> "inbox";

			for (Message message : newReceivedMessages) {
				Notification notification = new Notification(messagingMsgs.getString("newMessageReceived"));
				notification.setContents(messagingMsgs.getString("title") + ": " + message.getTitle());
				notification.setIcon("fa-envelope");
				notification.setDate(message.getSendingDate());
				notification.setRemover(() -> mm.markAsRead(message.getMessageID()));
				notification.setOutcomeSupplier(goToNewMessages);
				notification.setUpdater(updater);
				notifications.add(notification);
			}
		}

		Collections.sort(notifications);
	}

	public NotificationManager getNotificationManager() {
		return notificationManager;
	}

	public List<Demand> getNewDemands() {
		return newDemands;
	}

	public List<Article> getNewArticles() {
		return newArticles;
	}

	public List<Article> getUpdatedArticles() {
		return updatedArticles;
	}

	public List<Message> getNewReceivedMessages() {
		return newReceivedMessages;
	}

	public int getNotificationCount() {
		if (notifications != null) {
			return notifications.size();
		} else {
			return 0;
		}
	}

	public void removeAll() {
		if (notifications != null) {
			boolean notificationRemoved = false;
			for (Notification notification : notifications) {
				Runnable remover = notification.getRemover();
				if (remover != null) {
					try {
						remover.run();
						notificationRemoved = true;
					} catch (Exception e) {
						log.error("could not remove: " + notification, e);
					}
				}
			}

			if (notificationRemoved)
				Meta.broadcastOverViewScop(NotificationRemovedListener.class,
						NotificationRemovedListener::onNotificationRemoved);
		}
	}

	public List<Notification> getNotifications() {
		return notifications;
	}
}
