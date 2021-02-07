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
 * created on 2018/12/08
 * @header
 */

package com.rhcloud.cervex_jsoftware95.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.rhcloud.cervex_jsoftware95.beans.Util;

/**
 * The persistent class for the message database table.
 * 
 */
@Entity
@Table(name = "message")
@NamedQueries({ @NamedQuery(name = "Message.findAll", query = "SELECT m FROM Message m"),
		@NamedQuery(name = "Message.findByReceiver", query = "SELECT m FROM Message m where m.receiver=:receiver"),
		@NamedQuery(name = "Message.findByUserRec", query = "SELECT m.messageID FROM Message m where m.receiver=:user"),
		@NamedQuery(name = "Message.findByUserSen", query = "SELECT m.messageID FROM Message m where m.sender=:user"),
		@NamedQuery(name = "Message.findByUser", query = "SELECT m.messageID FROM Message m where m.sender=:user or m.receiver=:user") })
public class Message implements Serializable {

	private static final long serialVersionUID = -9030877910073095303L;

	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");

	public static byte NEW_MESSAGE = 0;
	public static byte OLD_MESSAGE = 1;

	@Id
	@Column(unique = true, nullable = false, length = 80)
	private String messageID;

	@Column(nullable = false)
	private byte state = NEW_MESSAGE;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date sendingDate;

	@Column(nullable = false, length = 10000)
	private String text;

	@Column(nullable = false, length = 40)
	private String title;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "receiverID", nullable = false)
	private User receiver;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "senderID", nullable = false)
	private User sender;

	public Message() {
		state = NEW_MESSAGE;
	}

	@PrePersist
	public void generatePrimaryKey() {
		sendingDate = Util.getCurrentTime();
		messageID = formater.format(sendingDate) + "-" + UUID.randomUUID();
	}

	public String getMessageID() {
		return this.messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public boolean isNew() {
		return state == NEW_MESSAGE;
	}

	public Date getSendingDate() {
		return this.sendingDate;
	}

	public void setSendingDate(Date sendingDate) {
		this.sendingDate = sendingDate;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public User getSender() {
		return this.sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	@Override
	public String toString() {
		return "Message [title=" + title + ", receiver=" + receiver.getUsername() + ", sender=" + sender.getUsername()
				+ "]";
	}

}
