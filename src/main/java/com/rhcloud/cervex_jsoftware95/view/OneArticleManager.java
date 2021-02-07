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
 * created on 2018/12/31
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.rhcloud.cervex_jsoftware95.beans.ArticleManager;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.validator.Version;

/**
 *
 * @author youcef debbah
 */
@Named
@ViewScoped
public class OneArticleManager implements Serializable {

	private static final long serialVersionUID = 1992824156640626846L;

	public static final String USERNAME_PARAMETER = "username";
	public static final String DEMAND_PARAMETERS = "demand";
	public static final String VERSION_PARAMETER = "version";
	public static final String PROGRESS_PARAMETER = "progress";
	public static final String ARTICLEID_PARAMETER = "articleID";

	private static final Logger log = Logger.getLogger(OneArticleManager.class);

	@EJB
	private ArticleManager articleManager;

	private String articleID;
	private String username;
	private String demandID;
	private String demandTitle;

	@Version
	private String version = "0.0.10";
	@Min(0)
	@Max(100)
	private int progress;

	@PostConstruct
	public void init() {
		Map<String, String[]> parameters = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterValuesMap();

		String[] usernameParamerter = parameters.get(USERNAME_PARAMETER);
		if (usernameParamerter != null && usernameParamerter.length > 0) {
			username = usernameParamerter[0];
		}

		String[] demandParameters = parameters.get(DEMAND_PARAMETERS);
		if (demandParameters != null && demandParameters.length > 0) {
			demandID = demandParameters[0];

			if (demandParameters.length > 1) {
				demandTitle = demandParameters[1];
			}
		}

		String[] versionParameter = parameters.get(VERSION_PARAMETER);
		if (versionParameter != null && versionParameter.length > 0) {
			version = versionParameter[0];
		}

		String[] progressParameter = parameters.get(PROGRESS_PARAMETER);
		if (progressParameter != null && progressParameter.length > 0) {
			try {
				progress = Integer.parseInt(progressParameter[0]);
			} catch (NumberFormatException e) {
				log.info("could not parse parameter: " + PROGRESS_PARAMETER + "=" + progressParameter[0]
						+ " as an integer");
			}
		}

		String[] articleIdParameter = parameters.get(ARTICLEID_PARAMETER);
		if (articleIdParameter != null && articleIdParameter.length > 0) {
			articleID = articleIdParameter[0];
		}

		log.info("addArticleManager initialized " + toString());
	}

	public String getArticleID() {
		return articleID;
	}

	public String getUsername() {
		return username;
	}

	public String getDemandID() {
		return demandID;
	}

	public String getDemandTitle() {
		return demandTitle;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void add() {
		String client = null;

		try {
			articleManager.addArticle(getUsername(), getDemandID(), getVersion(), getProgress());
			client = getUsername();

			log.info("succeed to add article to user: " + getUsername() + " from demand: " + getDemandID());
		} catch (Exception e) {
			Meta.handleInternalError(e);
		} finally {
			RequestContext.getCurrentInstance().closeDialog(client);
		}

	}

	public void update() {
		String client = null;

		try {
			String id = getArticleID();
			articleManager.updateArticle(id, getVersion(), getProgress());
			client = getUsername();

			log.info("succeed to update article: " + getArticleID());
		} catch (Exception e) {
			Meta.handleInternalError(e);
		} finally {
			RequestContext.getCurrentInstance().closeDialog(client);
		}
	}

	@Override
	public String toString() {
		return "AddArticleManager [username=" + username + ", demandID=" + demandID + "]";
	}

}
