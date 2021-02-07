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
import java.text.MessageFormat;
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

import com.rhcloud.cervex_jsoftware95.beans.ArticleManager;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.Demand;

/**
 *
 * @author youcef debbah
 */
@Named
@ViewScoped
public class ArticlesManager implements LocaleChangeListener, Serializable {

	private static final long serialVersionUID = 2326720361246038343L;

	private static final Logger log = Logger.getLogger(ArticlesManager.class);

	@EJB
	private ArticleManager articleManager;

	private List<Article> articles;
	private Article selectedArticle;

	private ResourceBundle articleMsgs;
	private ResourceBundle generalMsgs;

	@PostConstruct
	public void init() {
		initMsgs();
		log.info(ArticlesManager.class.getName() + " initialized");
	}

	private void initMsgs() {
		articleMsgs = MessageBundle.ARTICLE.getResource();
		generalMsgs = MessageBundle.GENERAL.getResource();
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		initMsgs();
	}

	public List<Article> getArticles() {
		if (articles == null) {
			update();
		}

		return articles;
	}

	public Article getSelectedArticle() {
		return selectedArticle;
	}

	public void setSelectedArticle(Article selectedArticle) {
		this.selectedArticle = selectedArticle;
	}

	private void update() {
		try {
			articles = articleManager.getAllArticles();
		} catch (Exception e) {
			articles = new ArrayList<>();
			Meta.handleInternalError(e);
		}
		updateSelection();
	}

	private void updateSelection() {
		if (selectedArticle != null) {
			String id = selectedArticle.getArticleID();
			List<Article> list = getArticles();
			selectedArticle = null;

			for (Article article : list) {
				if (article.getArticleID().equals(id)) {
					selectedArticle = article;
					return;
				}
			}
		}
	}

	public void updateArticle() {
		log.info("updating article: " + selectedArticle);
		if (selectedArticle != null) {
			Map<String, List<String>> params = new HashMap<>();

			Demand demand = selectedArticle.getDemand();
			params.put(OneArticleManager.DEMAND_PARAMETERS, Arrays.asList(demand.getDemandID(), demand.getTitle()));
			params.put(OneArticleManager.USERNAME_PARAMETER, Arrays.asList(selectedArticle.getUser().getUsername()));
			params.put(OneArticleManager.ARTICLEID_PARAMETER, Arrays.asList(selectedArticle.getArticleID()));
			params.put(OneArticleManager.PROGRESS_PARAMETER,
					Arrays.asList(String.valueOf(selectedArticle.getProgress())));
			params.put(OneArticleManager.VERSION_PARAMETER, Arrays.asList(selectedArticle.getVersion()));

			Map<String, Object> options = new HashMap<>();
			options.put("modal", true);
			options.put("width", 800);
			options.put("height", 330);
			options.put("contentWidth", "100%");
			options.put("contentHeight", "100%");
			options.put("maximizable", true);
			options.put("headerElement", "customheader");
			RequestContext.getCurrentInstance().openDialog("updateArticle", options, params);
		}
	}

	public void onArticleUpdate(SelectEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();

		if (event.getObject() != null) {
			String detail = MessageFormat.format(articleMsgs.getString("articleUpdatedDetail"), event.getObject());
			context.addMessage("global", new FacesMessage(articleMsgs.getString("articleUpdated"), detail));

			update();
		} else {
			context.addMessage("global", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					articleMsgs.getString("updatingArticleFailed"), generalMsgs.getString("tryAgainLater")));
		}

	}

	public void deleteArticle() {
		log.info("deleting article: " + selectedArticle);
		if (selectedArticle != null) {
			try {
				articleManager.deleteArticle(selectedArticle.getArticleID());
			} catch (Exception e) {
				Meta.handleInternalError(e);
			}
			update();
		}
	}

}
