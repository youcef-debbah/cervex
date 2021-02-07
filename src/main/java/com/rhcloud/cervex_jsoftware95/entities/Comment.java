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
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.rhcloud.cervex_jsoftware95.beans.Util;

/**
 * The persistent class for the comment database table.
 * 
 */
@Entity
@Table(name = "comment")
@NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c")
public class Comment implements Comparable<Comment>,Serializable {

	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");

	public static byte NEW_COMMENT = 0;
	public static byte AUTHORIZED_COMMENT = 1;

	@Id
	@Column(unique = true, nullable = false, length = 80)
	private String commentID;

	@Column(nullable = false, length = 500)
	private String content;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date date;

	@Column(nullable = false)
	private byte authorized;

	@ManyToOne
	@JoinColumn(name = "userid", nullable = true)
	private User user;

	// bi-directional many-to-one association to Blog
	@ManyToOne
	@JoinColumn(name = "blogID", nullable = false)
	private Blog blog;

	public Comment() {
		authorized = NEW_COMMENT;
	}

	@PrePersist
	public void generatePrimaryKey() {
		date = Util.getCurrentTime();
		commentID = formater.format(date) + "-" + UUID.randomUUID();
	}

	public String getCommentID() {
		return this.commentID;
	}

	public void setCommentID(String commentID) {
		this.commentID = commentID;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public byte getAuthorized() {
		return authorized;
	}

	public void setAuthorized(byte authorized) {
		this.authorized = authorized;
	}

	public boolean isNew() {
		return authorized == NEW_COMMENT;
	}

	public boolean isAuthorized() {
		return authorized == AUTHORIZED_COMMENT;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Blog getBlog() {
		return this.blog;
	}

	public void setBlog(Blog blog) {
		this.blog = blog;
	}
	
	@Override
	public String toString() {
		return "Comment [commentID=" + commentID + ", date=" + date + ", authorized=" + authorized + "]";
	}
	
	@Override
	public int compareTo(Comment other) {
		if (getDate() == null || other == null || other.getDate() == null)
			return 0;
		else
			return -getDate().compareTo(other.getDate());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blog == null) ? 0 : blog.hashCode());
		result = prime * result + ((commentID == null) ? 0 : commentID.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		if (blog == null) {
			if (other.blog != null)
				return false;
		} else if (!blog.equals(other.blog))
			return false;
		if (commentID == null) {
			if (other.commentID != null)
				return false;
		} else if (!commentID.equals(other.commentID))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}

}