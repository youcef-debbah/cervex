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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.rhcloud.cervex_jsoftware95.beans.UserManager;
import com.rhcloud.cervex_jsoftware95.control.OperationOutput;

@Named
@ViewScoped
public class DeleteUserDataManager implements Serializable {

	private static final long serialVersionUID = 6554500934382192675L;
	private static final Logger log = Logger.getLogger(DeleteUserDataManager.class);

	private String account;
	private boolean deleteComments;
	private String[] articlesToDelete;
	private String[] demandsToDelete;
	private String[] messagesSentToDelete;
	private String[] messagesReceivedToDelete;

	@EJB
	private UserManager userManager;

	@PostConstruct
	public void init() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String[]> params = context.getExternalContext().getRequestParameterValuesMap();
		articlesToDelete = params.get("articlesToDelete");
		demandsToDelete = params.get("demandsToDelete");
		messagesSentToDelete = params.get("messagesSentToDelete");
		messagesReceivedToDelete = params.get("messagesReceivedToDelete");
	}

	public void deleteAccount() {
		try {
			userManager.delete(getAccount(), isDeleteComments(), true);
			RequestContext.getCurrentInstance().closeDialog(OperationOutput.SUCCESS);
		} catch (Exception e) {
			log.error("cannot erase account: " + getAccount() + " (delete comments: " + isDeleteComments() + ")", e);
			RequestContext.getCurrentInstance().closeDialog(OperationOutput.FAILED);
		}
	}

	public void cancel() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		System.out.println("setting accout to:" + account);
		this.account = account;
	}

	public boolean isDeleteComments() {
		return deleteComments;
	}

	public void setDeleteComments(boolean deleteComments) {
		this.deleteComments = deleteComments;
	}

	public String[] getArticlesToDelete() {
		return articlesToDelete;
	}

	public void setArticlesToDelete(String[] articlesToDelete) {
		this.articlesToDelete = articlesToDelete;
	}

	public String[] getDemandsToDelete() {
		return demandsToDelete;
	}

	public void setDemandsToDelete(String[] demandsToDelete) {
		this.demandsToDelete = demandsToDelete;
	}

	public String[] getMessagesSentToDelete() {
		return messagesSentToDelete;
	}

	public void setMessagesSentToDelete(String[] messagesSentToDelete) {
		this.messagesSentToDelete = messagesSentToDelete;
	}

	public String[] getMessagesReceivedToDelete() {
		return messagesReceivedToDelete;
	}

	public void setMessagesReceivedToDelete(String[] messagesReceivedToDelete) {
		this.messagesReceivedToDelete = messagesReceivedToDelete;
	}

}
