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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.rhcloud.cervex_jsoftware95.beans.BlogManager;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.control.NotificationRemovedListener;
import com.rhcloud.cervex_jsoftware95.entities.Comment;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class NewCommentsView implements NotificationRemovedListener, Serializable {

	private static final long serialVersionUID = 1333044833655891074L;

	private List<Comment> newComments;
	private Comment selectedComment;
	
	@EJB
	private BlogManager blogManager;

	@PostConstruct
	public void init() {
		update();
	}

	private void update() {
		try {
			newComments = blogManager.getAllComments().stream().filter(Comment::isNew).collect(Collectors.toList());
		} catch (Exception e) {
			newComments = new ArrayList<>();
			Meta.handleInternalError(e);
		}
		
		updateSelection();
	}

	private void updateSelection() {
		if (selectedComment != null) {
			String id = selectedComment.getCommentID();
			selectedComment = null;
			List<Comment> list = getNewComments();
			
			if (list != null) {
				for (Comment comment : list) {
					if (Objects.equals(comment.getCommentID(), id)) {
						selectedComment = comment;
						return;
					}
				}
			}
		}
	}

	@Override
	public void onNotificationRemoved() {
		update();
	}
	
	public Comment getSelectedComment() {
		return selectedComment;
	}
	
	public void setSelectedComment(Comment selectedComment) {
		this.selectedComment = selectedComment;
	}

	public List<Comment> getNewComments() {
		return newComments;
	}

	public void setNewComments(List<Comment> newComments) {
		this.newComments = newComments;
	}
	
	public void authorizeComment() {
		Comment comment = getSelectedComment();
		if (comment != null) {
			try {
				blogManager.authorizeComment(comment.getCommentID());
				Meta.broadcastOverViewScop(NotificationRemovedListener.class,
						NotificationRemovedListener::onNotificationRemoved);
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}
	
	public void removeComment() {
		Comment comment = getSelectedComment();
		if (comment != null) {
			try {
				blogManager.removeComment(comment.getCommentID());
				Meta.broadcastOverViewScop(NotificationRemovedListener.class,
						NotificationRemovedListener::onNotificationRemoved);
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}

}
