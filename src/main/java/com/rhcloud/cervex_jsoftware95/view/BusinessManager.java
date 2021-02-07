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
 * created on 2018/01/08
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.rhcloud.cervex_jsoftware95.beans.ArticleApply;
import com.rhcloud.cervex_jsoftware95.beans.ArticleManager;
import com.rhcloud.cervex_jsoftware95.beans.UserManager;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.control.NotificationRemovedListener;
import com.rhcloud.cervex_jsoftware95.entities.ArticleState;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class BusinessManager implements NotificationRemovedListener, Serializable {

	private static final long serialVersionUID = -1486015975633744695L;

	@EJB
	private UserManager userManager;

	@EJB
	private ArticleManager articleManager;

	private List<ArticleApply> applies;

	@Override
	public void onNotificationRemoved() {
		update();
	}

	public List<ArticleApply> getApplies() {
		if (applies == null) {
			update();
		}

		return applies;
	}

	private void update() {
		try {
			applies = userManager.getApplies(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
		} catch (Exception e) {
			applies = new ArrayList<>();
			Meta.handleInternalError(e);
		}
	}

	public void onArticleToggle(String articleID, String articleState) {
		if (articleID != null && !ArticleState.OLD_ARTICLE.getStateName().equalsIgnoreCase(articleState)) {
			try {
				articleManager.markAsOldArticle(articleID);
				Meta.broadcastOverViewScop(NotificationRemovedListener.class,
						NotificationRemovedListener::onNotificationRemoved);
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}
}
