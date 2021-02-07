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

package com.rhcloud.cervex_jsoftware95.beans;

import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.entities.Message;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

/**
 * 
 * @author youcef debbah
 */
@Stateless
public class MessageManagerBean implements MessageManager {

	private static Logger log = Logger.getLogger(MessageManagerBean.class);

	@PersistenceContext(unitName = "cervex")
	EntityManager em;

	@EJB
	UserManager userManager;

	public static void checkIntegrity(Message message) {

		Objects.requireNonNull(message);
		if (message.getTitle() == null || message.getText() == null) {
			throw new InformationSystemException("Illegal message: " + message);
		}

		UserManagerBean.checkIntegrity(message.getSender());
		UserManagerBean.checkIntegrity(message.getReceiver());
	}

	@Override
	public void sendMessage(String senderUsername, String receiverUsername, String title, String text) {
		Objects.requireNonNull(senderUsername);
		Objects.requireNonNull(receiverUsername);
		Objects.requireNonNull(title);
		Objects.requireNonNull(text);

		try {
			log.debug("trying to send message: " + title);
			Message message = new Message();
			User sender = userManager.getUser(senderUsername);
			sender.addSendedMessage(message);

			User receiver = userManager.getUser(receiverUsername);
			receiver.addReceivedMessages(message);

			message.setSender(sender);
			message.setReceiver(receiver);
			message.setTitle(title);
			message.setText(text);

			em.persist(message);
			log.debug("message sent: " + title);
		} catch (Exception e) {
			throw new EJBException("Could not send message: senderUsername=" + senderUsername + ", receiverUsername="
					+ receiverUsername + ", title=" + title, e);
		}
	}

	@Override
	public List<Message> getMessagesFor(String userID) {
		Objects.requireNonNull(userID);

		try {
			log.debug("trying to retrieve messages for user: " + userID);
			User receiver = em.find(User.class, userID);
			List<Message> messages = em.createNamedQuery("Message.findByReceiver", Message.class)
					.setParameter("receiver", receiver).getResultList();
			log.debug("messages retrieved (" + messages.size() + ") for user: " + userID);
			return messages;
		} catch (Exception e) {
			throw new EJBException("Could not retrieve messages for user: " + userID);
		}
	}

	@Override
	public void markAsRead(String messageID) {
		Objects.requireNonNull(messageID);

		Message message;

		try {
			log.debug("trying to mark message as read: " + messageID);
			message = em.find(Message.class, messageID);
		} catch (Exception e) {
			throw new EJBException("Error while trying to find message: " + messageID, e);
		}

		if (message == null)
			throw new InformationSystemException("message not found: " + messageID);

		if (message.isNew()) {
			try {
				message.setState(Message.OLD_MESSAGE);
				em.merge(message);
				log.debug("message marked as read: " + messageID);
			} catch (Exception e) {
				throw new EJBException("Could not mark message as read: " + messageID, e);
			}
		}
	}

}
