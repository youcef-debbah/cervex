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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.beans.BlogManager;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.control.NotificationRemovedListener;
import com.rhcloud.cervex_jsoftware95.control.PageServer;
import com.rhcloud.cervex_jsoftware95.entities.Blog;
import com.rhcloud.cervex_jsoftware95.entities.Comment;
import com.rhcloud.cervex_jsoftware95.security.SecurityManager;
import com.rhcloud.cervex_jsoftware95.util.WebKit;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class BlogView implements NotificationRemovedListener, Serializable {

	private static final long serialVersionUID = -2677914787956301386L;
	private static final Logger log = Logger.getLogger(BlogView.class);

	private String blogID;
	private Blog blog;
	private List<Comment> comments;

	@Size(min = 1, max = 500)
	private String newComment;

	private ResourceBundle blogMsgs;

	@EJB
	private BlogManager blogManager;

	@PostConstruct
	public void init() {
		blogMsgs = MessageBundle.BLOG.getResource();
	}

	public String getBlogID() {
		return blogID;
	}

	public void setBlogID(String blogID) {
		this.blogID = blogID;
	}

	public String getNewComment() {
		return newComment;
	}

	public void setNewComment(String newComment) {
		this.newComment = newComment;
	}

	public Blog getBlog() {
		if (blog == null || blog.getBlogID() == null || !blog.getBlogID().equals(getBlogID()))
			update();
		return blog;
	}

	private void update() {
		String id = getBlogID();
		if (id != null) {
			try {
				blog = blogManager.getBlogWithComments(id);
				comments = blog.getComments().stream().filter(Comment::isAuthorized).sorted()
						.collect(Collectors.toList());
			} catch (Exception e) {
				blog = null;
				Meta.handleInternalError(e);
			}
		} else {
			blog = null;
		}
	}
	
	@Override
	public void onNotificationRemoved() {
		update();
	}

	public String getPostURI() {
		String blogID = getBlogID();
		if (blogID == null)
			return null;

		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put(PageServer.BLOG, blogID);
			try {
				parameters.put(PageServer.LANG,
						FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage());
			} catch (Exception e) {
				log.error("could not get locale from view root", e);
			}
			return WebKit.buildURI(PageServer.PAGE_SERVER_PATTERN, parameters);
		} catch (URISyntaxException e) {
			log.error("cannot build poster URI for blog: " + getBlogID());
			return null;
		}
	}

	public List<Comment> getComments() {
		return comments;
	}

	public String removeBlog() {
		String id = getBlogID();
		if (id != null) {
			blogManager.deleteBlog(getBlogID());
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_INFO, blogMsgs.getString("postRmoved"),
					blogMsgs.getString("postRemovedSuccessfully")));
			context.getExternalContext().getFlash().setKeepMessages(true);
		}
		return "posts";
	}

	public void removeComment(String commentID) {
		if (commentID != null) {
			try {
				blogManager.removeComment(commentID);
				update();
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}

	public void postNewComment() {
		FacesContext context = FacesContext.getCurrentInstance();
		String newComment = getNewComment();
		if (newComment == null || newComment.isEmpty()) {
			context.addMessage("blogForm:newComment", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					blogMsgs.getString("emptyComment"), blogMsgs.getString("writeSomthing")));
			return;
		}

		try {
			HttpServletRequest request = WebKit.getRequest();
			boolean validationNeeded = !request.isUserInRole(SecurityManager.ADMIN_ROLE);
			blogManager.addComment(newComment, getBlog().getBlogID(), request.getRemoteUser(), validationNeeded);
			setNewComment(null);
			update();
			if (validationNeeded)
				context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_INFO,
						blogMsgs.getString("commentSent"), blogMsgs.getString("commentPending")));
		} catch (Exception e) {
			Meta.handleInternalError(e);
			return;
		}
	}
}
