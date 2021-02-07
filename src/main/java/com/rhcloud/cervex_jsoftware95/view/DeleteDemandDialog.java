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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.rhcloud.cervex_jsoftware95.beans.ArticleManager;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.entities.Demand;

/**
 *
 * @author youcef debbah
 */
@Named
@ViewScoped
public class DeleteDemandDialog implements Serializable {

	private static final long serialVersionUID = -2011247313373904905L;
	private static final Logger log = Logger.getLogger(DeleteDemandDialog.class);
	
	public enum Output { ARTICLE_DELETED, NO_ARTICLE_DELETED, CANCELED }

	private String demandID;
	private Demand demand;

	@EJB
	ArticleManager articleManager;

	@PostConstruct
	public void init() {
		//TODO
		System.out.println("### delete demand dialog init: " + demand);
	}

	public Demand getDemand() {
		return demand;
	}

	public String getDemandID() {
		return demandID;
	}

	public void setDemandID(String demandID) {
		System.out.println("### setting demand to: " + demandID);
		try {
			demand = articleManager.getDemand(demandID);
			this.demandID = demandID;
		} catch (Exception e) {
			demand = null;
			this.demandID = null;
			log.error("could not found demand to be deleted: " + demandID, e);
		}
	}

	public void deleteDemand() {
		System.out.println("### deleting demand: " + demand);
		if (demandID == null) {
			cancel();
		} else {
			Output output = null;
			try {
				boolean articleDeleted = articleManager.deleteDemand(demandID);
				output = articleDeleted? Output.ARTICLE_DELETED : Output.NO_ARTICLE_DELETED ;
			} catch (Exception e) {
				Meta.handleInternalError(e);
			} finally {
				RequestContext.getCurrentInstance().closeDialog(output);
			}
		}
	}

	public void cancel() {
		RequestContext.getCurrentInstance().closeDialog(Output.CANCELED);
	}

}
