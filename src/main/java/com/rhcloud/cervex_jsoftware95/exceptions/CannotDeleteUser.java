package com.rhcloud.cervex_jsoftware95.exceptions;

import java.util.List;

public class CannotDeleteUser extends InformationSystemException {

	private static final long serialVersionUID = 7844241481381704702L;

	private List<String> demandsToDelete;
	private List<String> articlesToDelete;
	private List<String> messagesReceivedToDelete;
	private List<String> messagesSentToDelete;

	public CannotDeleteUser(List<String> demandsToDelete, List<String> articlesToDelete,
			List<String> messagesReceivedToDelete, List<String> messagesSentToDelete) {

		super("can not delete this user, first delete related data");
		this.demandsToDelete = demandsToDelete;
		this.articlesToDelete = articlesToDelete;
		this.messagesReceivedToDelete = messagesReceivedToDelete;
		this.messagesSentToDelete = messagesSentToDelete;
	}

	public List<String> getDemandsToDelete() {
		return demandsToDelete;
	}

	public void setDemandsToDelete(List<String> demandsToDelete) {
		this.demandsToDelete = demandsToDelete;
	}

	public List<String> getArticlesToDelete() {
		return articlesToDelete;
	}

	public void setArticlesToDelete(List<String> articlesToDelete) {
		this.articlesToDelete = articlesToDelete;
	}

	public List<String> getMessagesReceivedToDelete() {
		return messagesReceivedToDelete;
	}

	public void setMessagesReceivedToDelete(List<String> messagesReceivedToDelete) {
		this.messagesReceivedToDelete = messagesReceivedToDelete;
	}

	public List<String> getMessagesSentToDelete() {
		return messagesSentToDelete;
	}

	public void setMessagesSentToDelete(List<String> messagesSentToDelete) {
		this.messagesSentToDelete = messagesSentToDelete;
	}
}
