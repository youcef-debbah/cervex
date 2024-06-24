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
 * created on 2020/06/13
 * @header
 */

package com.rhcloud.cervex_jsoftware95.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.rhcloud.cervex_jsoftware95.beans.Util;

/**
 * 
 * @author youcef debbah
 */
@Entity
@Table(name = "file")
@NamedQueries({ @NamedQuery(name = "File.findAll", query = "SELECT f FROM File f"),
		@NamedQuery(name = "File.findByDemand", query = "SELECT f.fileID FROM File f WHERE f.demand=:demand") })
public class File implements Serializable {

	private static final long serialVersionUID = -7133410117981311430L;

	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");

	@Id
	@Column(unique = true, nullable = false, length = 80)
	private String fileID;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(nullable = false, columnDefinition = "mediumblob")
	private byte[] contents;

	@Column(nullable = false, length = 40)
	private String contentType;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date uploadDate;

	@Column(nullable = false, length = 80)
	private String name;

	@Column(nullable = false)
	private long size;

	@ManyToOne
	@JoinColumn(name = "demandID", nullable = true)
	private Demand demand;

	@ManyToOne
	@JoinColumn(name = "blogID", nullable = true)
	private Blog blog;

	@PrePersist
	public void generatePrimaryKey() {
		if (uploadDate == null)
			uploadDate = Util.getCurrentTime();
		if (fileID == null)
			fileID = formater.format(uploadDate) + "-" + UUID.randomUUID();
	}

	public String getfileID() {
		return this.fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public byte[] getContents() {
		return contents;
	}

	public void setContents(byte[] contents) {
		this.contents = contents;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getName() {
		return name;
	}

	public void setDemand(Demand demand) {
		this.demand = demand;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Demand getDemand() {
		return demand;
	}

	public void setBlog(Blog blog) {
		this.blog = blog;
	}

	public Blog getBlog() {
		return this.blog;
	}

	@Override
	public String toString() {
		return "File [fileID=" + fileID + ", name=" + name + ", demand="
				+ ((demand != null) ? demand.getDemandID() : "null") + ", blog=" + ((blog != null)? blog.getBlogID() : "null") + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (size ^ (size >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof File))
			return false;

		File other = (File) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		if (size != other.size)
			return false;

		return true;
	}

}
