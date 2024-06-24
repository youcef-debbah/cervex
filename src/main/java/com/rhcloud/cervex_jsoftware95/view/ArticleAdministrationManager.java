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
 * created on 2018/12/30
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.text.MessageFormat;
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

import com.rhcloud.cervex_jsoftware95.beans.ArticleApply;
import com.rhcloud.cervex_jsoftware95.beans.ArticleManager;
import com.rhcloud.cervex_jsoftware95.beans.UserManager;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.control.NotificationRemovedListener;
import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.util.SmartKit;
import com.rhcloud.cervex_jsoftware95.util.WebKit;

/**
 *
 * @author youcef debbah
 */
@Named
@ViewScoped
public class ArticleAdministrationManager implements LocaleChangeListener, NotificationRemovedListener, Serializable {

	private static final long serialVersionUID = 7987797752935077844L;

	private static final Logger log = Logger.getLogger(ArticleAdministrationManager.class);

	@EJB
	private UserManager userManager;

	@EJB
	private ArticleManager articleManager;

	private ArticleApply selectedApply;
	private List<ArticleApply> applies;

	private ResourceBundle articleMsgs;
	private ResourceBundle generalMsgs;
	private boolean onlyNew;

	@PostConstruct
	public void init() {
		initMsgs();
	}

	private void initMsgs() {
		articleMsgs = MessageBundle.ARTICLE.getResource();
		generalMsgs = MessageBundle.GENERAL.getResource();
		log.info(ArticleAdministrationManager.class.getName() + " initialized");
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		initMsgs();
	}

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

	public void replay(ArticleApply apply) {
		log.info("trying to replay on: " + apply);
	}

	public void openApplyDeleteDialog() {
		ArticleApply apply = getSelectedApply();
		log.info("deleting apply: " + apply);
		if (apply != null) {
			Map<String, List<String>> params = new HashMap<>();
			params.put(OneArticleManager.DEMAND_PARAMETERS, Arrays.asList(apply.getDemand().getDemandID()));

			Map<String, Object> options = new HashMap<>();
			options.put("modal", true);
			options.put("width", 600);
			options.put("height", 200);
			options.put("contentWidth", "100%");
			options.put("contentHeight", "100%");
			options.put("maximizable", true);
			options.put("headerElement", "customheader");
			RequestContext.getCurrentInstance().openDialog("deleteDemandDialog", options, params);
		}
	}

	public void onApplyDelete(SelectEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = null;
		String detail;

		Object output = event.getObject();

		if (output != null) {
			if (output != DeleteDemandDialog.Output.CANCELED) {
				if (output == DeleteDemandDialog.Output.ARTICLE_DELETED)
					detail = articleMsgs.getString("demandAndArticleDeletedDetail");
				else
					detail = articleMsgs.getString("demandDeletedDetail");

				message = new FacesMessage(articleMsgs.getString("demandDeleted"), detail);
				update();
			}
		} else {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, articleMsgs.getString("deletingDemandFailed"),
					generalMsgs.getString("tryAgainLater"));
		}

		if (message != null)
			context.addMessage("global", message);
	}

	public void newArticle() {
		ArticleApply apply = getSelectedApply();
		log.info("adding article to apply: " + apply);
		if (apply != null && apply.getArticle() == null) {
			Map<String, List<String>> params = new HashMap<>();
			params.put(OneArticleManager.USERNAME_PARAMETER, Arrays.asList(apply.getUser().getUsername()));

			Demand demand = apply.getDemand();
			params.put(OneArticleManager.DEMAND_PARAMETERS, Arrays.asList(demand.getDemandID(), demand.getTitle()));

			Map<String, Object> options = new HashMap<>();
			options.put("modal", true);
			options.put("width", 800);
			options.put("height", 330);
			options.put("contentWidth", "100%");
			options.put("contentHeight", "100%");
			options.put("maximizable", true);
			options.put("headerElement", "customheader");
			RequestContext.getCurrentInstance().openDialog("addArticle", options, params);
		}
	}

	public void onDialogClose(SelectEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();

		if (event.getObject() != null) {
			String detail = MessageFormat.format(articleMsgs.getString("articleAddedDetail"), event.getObject());
			context.addMessage("global", new FacesMessage(articleMsgs.getString("articleAdded"), detail));

			update();
		} else {
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					articleMsgs.getString("addingArticleFailed"), generalMsgs.getString("tryAgainLater")));
		}
	}

	public ArticleApply getSelectedApply() {
		return selectedApply;
	}

	private void update() {
		try {
			applies = userManager.getAllApplies();
			if (isOnlyNew()) {
				applies = applies.stream().filter(apply -> apply.getDemand().isNew()).collect(Collectors.toList());
			}
		} catch (Exception e) {
			Meta.handleInternalError(e);
			applies = new ArrayList<>();
		}

		updateSelection();
	}

	private void updateSelection() {
		if (selectedApply != null) {
			String id = selectedApply.getDemand().getDemandID();
			List<ArticleApply> list = getApplies();
			selectedApply = null;

			if (list != null) {
				for (ArticleApply apply : list) {
					if (apply.getDemand().getDemandID().equals(id)) {
						selectedApply = apply;
						return;
					}
				}
			}
		}
	}

	public void onOnlyNewClick(AjaxBehaviorEvent event) {
		update();
	}

	private void markDemandAsOld(String demandID) {
		if (demandID != null) {
			try {
				articleManager.markAsOldDemand(demandID);
				Meta.broadcastOverViewScop(NotificationRemovedListener.class,
						NotificationRemovedListener::onNotificationRemoved);
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}

	public void onApplyToggle() {
		String demandID = WebKit.getRequest().getParameter("id");
		markDemandAsOld(demandID);
	}

	public void setSelectedApply(ArticleApply selectedApply) {
		this.selectedApply = selectedApply;
		if (selectedApply != null && selectedApply.getDemand().isNew()) {
			markDemandAsOld(selectedApply.getDemand().getDemandID());
		}
	}

	public void setOnlyNew(boolean onlyNew) {
		this.onlyNew = onlyNew;
	}

	public boolean isOnlyNew() {
		return onlyNew;
	}

	public String formatSize(long bytes) {
		return SmartKit.formatSize(bytes, generalMsgs.getString("byteSymbol"));
	}

	public void removeFile(String fileID) {
		if (fileID != null) {
			try {
				articleManager.removeFile(fileID);
				update();
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
		}
	}
}
