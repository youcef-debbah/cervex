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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.rhcloud.cervex_jsoftware95.beans.UserManager;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeEvent;
import com.rhcloud.cervex_jsoftware95.control.LocaleChangeListener;
import com.rhcloud.cervex_jsoftware95.control.MessageBundle;
import com.rhcloud.cervex_jsoftware95.control.OperationOutput;
import com.rhcloud.cervex_jsoftware95.exceptions.CannotDeleteUser;

@Named
@ViewScoped
public class DeleteUserManager implements LocaleChangeListener, Serializable {

	private static final long serialVersionUID = -9064934505640698643L;
	private static final Logger log = Logger.getLogger(DeleteUserManager.class);

	@EJB
	private UserManager userManager;

	private ResourceBundle userMsgs;

	private String account;
	private boolean deleteComments;

	@PostConstruct
	public void init() {
		initMsgs();
		log.info(getClass().getName() + " initialized");
	}

	@Override
	public void installNewLocale(LocaleChangeEvent event) {
		initMsgs();
	}

	private void initMsgs() {
		userMsgs = MessageBundle.USER.getResource();
	}

	public void deleteAccount() {
		if (account != null) {
			try {
				userManager.delete(account, deleteComments, false);
				RequestContext.getCurrentInstance().closeDialog(OperationOutput.SUCCESS);
			} catch (CannotDeleteUser e) {
				Map<String, List<String>> params = new HashMap<>();
				int total = 0;

				if (e.getArticlesToDelete() != null) {
					total += e.getArticlesToDelete().size();
					params.put("articlesToDelete", e.getArticlesToDelete());
				}

				if (e.getDemandsToDelete() != null) {
					total += e.getDemandsToDelete().size();
					params.put("demandsToDelete", e.getDemandsToDelete());
				}

				if (e.getMessagesSentToDelete() != null) {
					total += e.getMessagesSentToDelete().size();
					params.put("messagesSentToDelete", e.getMessagesSentToDelete());
				}

				if (e.getMessagesReceivedToDelete() != null) {
					total += e.getMessagesReceivedToDelete().size();
					params.put("messagesReceivedToDelete", e.getMessagesReceivedToDelete());
				}

				if (total > 0) {
					params.put("account", Arrays.asList(account));
					params.put("deleteComments", Arrays.asList(String.valueOf(deleteComments)));
					openDeleteUserDataDialog(params);
				} else {
					log.error("empty CannotDeleteUser exception while deleting: " + account, e);
					RequestContext.getCurrentInstance().closeDialog(OperationOutput.FAILED);
				}

			} catch (Exception e) {
				log.error("cannot delete account: " + getAccount() + " (delete comments: " + isDeleteComments() + ")",
						e);
				RequestContext.getCurrentInstance().closeDialog(OperationOutput.FAILED);
			}
		}
	}

	private void openDeleteUserDataDialog(Map<String, List<String>> params) {
		Map<String, Object> options = new HashMap<>();
		options.put("modal", true);
		options.put("width", 600);
		options.put("height", 280);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		options.put("headerElement", userMsgs.getString("deleteUser"));

		RequestContext.getCurrentInstance().openDialog("deleteAccountDataDialog", options, params);
	}

	public void cancel() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	public void onUserDelete(SelectEvent event) {
		RequestContext.getCurrentInstance().closeDialog(event.getObject());
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public boolean isDeleteComments() {
		return deleteComments;
	}

	public void setDeleteComments(boolean deleteComments) {
		this.deleteComments = deleteComments;
	}

}
