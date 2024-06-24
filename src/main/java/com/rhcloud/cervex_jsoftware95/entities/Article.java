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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.rhcloud.cervex_jsoftware95.beans.Util;

/**
 * The persistent class for the article database table.
 */
@Entity
@Table(name = "article")
@NamedQueries({ @NamedQuery(name = "Article.findAll", query = "SELECT a FROM Article a"),
		@NamedQuery(name = "Article.findByUser", query = "SELECT a.articleID FROM Article a Where a.user=:user") })
public class Article implements Serializable {

	private static final long serialVersionUID = 8075940971787747297L;
	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");

	@Id
	@Column(unique = true, nullable = false, length = 80)
	private String articleID;

	@Temporal(TemporalType.DATE)
	@Column(updatable = false, nullable = false)
	private Date creationDate;

	@Column(nullable = false)
	private int progress;

	@Column(nullable = false, length = 10)
	private String version;

	@Column(nullable = false, length = 10)
	private String state;

	@Temporal(TemporalType.DATE)
	@Column(updatable = true, nullable = true)
	private Date lastUpdate;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "userID", nullable = false)
	private User user;

	@OneToOne
	@JoinColumn(name = "demandID", nullable = false)
	private Demand demand;

	public Article() {
		state = ArticleState.NEW_ARTICLE.getStateName();
	}

	@PrePersist
	public void generatePrimaryKey() {
		creationDate = Util.getCurrentTime();
		articleID = formater.format(creationDate) + "-" + UUID.randomUUID();
	}

	public String getArticleID() {
		return this.articleID;
	}

	public void setArticleID(String articleID) {
		this.articleID = articleID;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getProgress() {
		return this.progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Demand getDemand() {
		return demand;
	}

	public void setDemand(Demand demand) {
		this.demand = demand;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public boolean isNew() {
		return ArticleState.NEW_ARTICLE.getStateName().equalsIgnoreCase(state);
	}

	public boolean isUpdated() {
		return ArticleState.UPDATED_ARTICLE.getStateName().equalsIgnoreCase(state);
	}

	public boolean isOld() {
		return ArticleState.OLD_ARTICLE.getStateName().equalsIgnoreCase(state);
	}

	@Override
	public String toString() {
		return "Article [version=" + version + ", user=" + user.getUsername() + "]";
	}

}
