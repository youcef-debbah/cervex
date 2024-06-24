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
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.rhcloud.cervex_jsoftware95.beans.Util;

/**
 * The persistent class for the user database table.
 *
 */
@Entity
@Table(name = "user")
@NamedQueries({ @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
	@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username=:username"),
	@NamedQuery(name = "User.findUsername", query = "SELECT u.username FROM User u WHERE u.username=:username"),
	@NamedQuery(name = "User.findByUsernameFetched", query = "SELECT u FROM User u join fetch u.demands  WHERE u.username=:username "),
	@NamedQuery(name = "User.findEmail", query = "SELECT u.email FROM User u WHERE u.email=:email") })
public class User implements Serializable {
    
    private static final long serialVersionUID = -4745109210124942526L;

    private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");

    @Id
    @Column(unique = true, nullable = false, length = 80)
    private String userID;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date creationDate;

    @Column(nullable = false, length = 60)
    private String email;

    @Column(length = 100)
    private String enterpriseAddress;

    @Column(length = 50)
    private String enterpriseName;

    @Column(nullable = false, length = 40)
    private String password;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, length = 40)
    private String username;

	@Column(nullable = false, length = 200)
	private String website;

    // bi-directional many-to-one association to Article
    @OneToMany(mappedBy = "user")
    private List<Article> articles;

    // bi-directional many-to-one association to Demand
    @OneToMany(mappedBy = "user")
    private List<Demand> demands;

    // bi-directional many-to-one association to Message
    @OneToMany(mappedBy = "receiver")
    private List<Message> receivedMessages;

    // bi-directional many-to-one association to Message
    @OneToMany(mappedBy = "sender")
    private List<Message> sendedMessages;

    public User() {
    }

    @PrePersist
    public void generatePrimaryKey() {
		creationDate = Util.getCurrentTime();
	userID = formater.format(creationDate) + "-" + UUID.randomUUID();
    }

    public String getUserID() {
	return userID;
    }

    public void setUserID(String userID) {
	this.userID = userID;
    }

    public Date getCreationDate() {
	return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
	this.creationDate = creationDate;
    }

    public String getEmail() {
	return this.email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getEnterpriseAddress() {
	return this.enterpriseAddress;
    }

    public void setEnterpriseAddress(String enterpriseAddress) {
	this.enterpriseAddress = enterpriseAddress;
    }

    public String getEnterpriseName() {
	return this.enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
	this.enterpriseName = enterpriseName;
    }

    public String getPassword() {
	return this.password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getPhoneNumber() {
	return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    public String getRole() {
	return this.role;
    }

    public void setRole(String role) {
	this.role = role;
    }

    public String getUsername() {
	return this.username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

    public List<Article> getArticles() {
	return this.articles;
    }

    public void setArticles(List<Article> articles) {
	this.articles = articles;
    }

    public Article addArticle(Article article) {
	getArticles().add(article);
	article.setUser(this);

	return article;
    }

    public Article removeArticle(Article article) {
	getArticles().remove(article);
	article.setUser(null);

	return article;
    }

    public List<Demand> getDemands() {
	return demands;
    }

    public void setDemands(List<Demand> demands) {
	this.demands = demands;
    }

    public Demand addDemand(Demand demand) {
	getDemands().add(demand);
	demand.setUser(this);

	return demand;
    }

    public Demand removeDemand(Demand demand) {
	getDemands().remove(demand);
	demand.setUser(null);

	return demand;
    }

    public List<Message> getReceivedMessages() {
	return receivedMessages;
    }

    public void setReceivedMessages(List<Message> receivedMessages) {
	this.receivedMessages = receivedMessages;
    }

    public Message addReceivedMessages(Message receivedMessage) {
	getReceivedMessages().add(receivedMessage);
	receivedMessage.setReceiver(this);

	return receivedMessage;
    }

    public Message removeReceivedMessage(Message receivedMessage) {
	getReceivedMessages().remove(receivedMessage);
	receivedMessage.setReceiver(null);

	return receivedMessage;
    }

    public List<Message> getSendedMessages() {
	return sendedMessages;
    }

    public void setSendedMessages(List<Message> sendedMessages) {
	this.sendedMessages = sendedMessages;
    }

    public Message addSendedMessage(Message sendedMessage) {
	getSendedMessages().add(sendedMessage);
	sendedMessage.setSender(this);

	return sendedMessage;
    }

    public Message removeSendedMessage(Message sendedMessage) {
	getSendedMessages().remove(sendedMessage);
	sendedMessage.setSender(null);

	return sendedMessage;
    }

    @Override
    public String toString() {
	return "User [userID=" + userID + ", username=" + username + ", email=" + email + "]";
    }

}
