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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.rhcloud.cervex_jsoftware95.beans.DemandStaticRecord;
import com.rhcloud.cervex_jsoftware95.beans.Util;

/**
 * The persistent class for the demand database table.
 * 
 */
@Entity
@Table(name = "demand")
@NamedQueries({

		@NamedQuery(name = "Demand.findAll", query = "SELECT d FROM Demand d"),
		@NamedQuery(name = "Demand.findByType", query = "SELECT d.type,count(d.type) FROM Demand d group by d.type"),
		@NamedQuery(name = "Demand.findAttachedFiles", query = "SELECT d FROM Demand d join fetch d.attachedFiles WHERE d.demandID=:demandID"),
		@NamedQuery(name = "Demand.findCount", query = "SELECT count(d.demandID) FROM Demand d"),
		@NamedQuery(name = "Demand.findCountPending", query = "SELECT count(d.demandID) FROM Demand d WHERE d.demandID NOT IN (SELECT a.demand FROM Article a)"),
		@NamedQuery(name = "Demand.findByUser", query = "SELECT d.demandID FROM Demand d Where d.user=:user") })

@SqlResultSetMapping(

		name = "DemandStaticRecordMapping", classes = @ConstructorResult(targetClass = DemandStaticRecord.class, columns = {

				@ColumnResult(name = "applyType", type = String.class),
				@ColumnResult(name = "applyCount", type = Long.class)

		})

)

@NamedNativeQuery(name = "getDemandStaticRecord", resultClass = DemandStaticRecord.class, query = "SELECT demand.type as applyType, count(demand.type) as applyCount FROM demand group by demand.type", resultSetMapping = "DemandStaticRecordMapping")

public class Demand implements Serializable {

	private static final long serialVersionUID = 8588195923499904131L;
	public static byte NEW_DEMAND = 0;
	public static byte OLD_DEMAND = 1;

	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");

	@Id
	@Column(unique = true, nullable = false, length = 80)
	private String demandID;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date demandDate;

	@Column(nullable = false, length = 10000)
	private String description;

	@Column(nullable = false, length = 40)
	private String title;

	@Column(nullable = false, length = 20)
	private String type;

	@Column(nullable = false)
	private byte state;

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false)
	private User user;

	@OneToOne(mappedBy = "demand")
	private Article article;

	@OneToMany(mappedBy = "demand", fetch = FetchType.EAGER)
	private List<File> attachedFiles;

	public Demand() {
		attachedFiles = new ArrayList<>();
	}

	@PrePersist
	public void generatePrimaryKey() {
		demandDate = Util.getCurrentTime();
		state = NEW_DEMAND;
		demandID = formater.format(demandDate) + "-" + UUID.randomUUID();
	}

	public String getDemandID() {
		return this.demandID;
	}

	public void setDemandID(String demandID) {
		this.demandID = demandID;
	}

	public Date getDemandDate() {
		return this.demandDate;
	}

	public void setDemandDate(Date demandDate) {
		this.demandDate = demandDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public boolean isNew() {
		return state == NEW_DEMAND;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public List<File> getAttachedFiles() {
		return attachedFiles;
	}

	public void setAttachedFiles(List<File> attachedFiles) {
		this.attachedFiles = attachedFiles;
	}

	public File addAttachedFile(File attachedFile) {
		getAttachedFiles().add(attachedFile);
		attachedFile.setDemand(this);
		return attachedFile;
	}

	public File removeAttachedFile(File attachedFile) {
		getAttachedFiles().remove(attachedFile);
		attachedFile.setDemand(null);

		return attachedFile;
	}

	@Override
	public String toString() {
		return "Demand [title=" + title + ", type=" + type + ", user=" + user.getUsername() + "]";
	}

}
