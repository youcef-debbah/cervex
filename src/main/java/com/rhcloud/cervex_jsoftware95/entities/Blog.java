package com.rhcloud.cervex_jsoftware95.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.rhcloud.cervex_jsoftware95.beans.Util;


/**
 * The persistent class for the blog database table.
 * 
 */
@Entity
@Table(name="blog")
@NamedQueries({
	@NamedQuery(name="Blog.findAll", query="SELECT b FROM Blog b"),
	@NamedQuery(name="Blog.findFetchedComments",query="SELECT b FROM Blog b JOIN FETCH b.comments WHERE b.blogID=:blogID"),
	@NamedQuery(name="Blog.findFetchedFiles",query="SELECT b FROM Blog b JOIN FETCH b.attachedFiles WHERE b.blogID=:blogID")
})
public class Blog implements Serializable, Comparable<Blog> {

	private static final long serialVersionUID = -3977044324355297887L;
	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
	public static byte DRAFT = 1;
	public static byte POSTED = 0;

	@Id
	@Column(unique=true, nullable=false, length=80)
	private String blogID;

	@Column(length=10000)
	private String content;

	@Column(nullable=false, length = 5000)
	private String intro;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date date;

	@Column(nullable=false, length=200)
	private String title;
	
	@Column(nullable = false)
	private byte draft;
	
	@ManyToOne
	@JoinColumn(name="userID")
	private User user;

	//bi-directional many-to-one association to Comment
	@OneToMany(mappedBy="blog")
	private List<Comment> comments;
	
	@OneToMany(mappedBy="blog")
	private List<File> attachedFiles;
	
	@OneToOne
	@JoinColumn(name = "thumbnail", nullable=true, insertable=true, updatable=true)
	private File thumbnail; 

	public Blog() {
		attachedFiles = new ArrayList<>();
	}
	
	@PrePersist
	public void generatePrimaryKey(){
		date = Util.getCurrentTime();
		blogID=formater.format(date)+ "-" + UUID.randomUUID();
	}

	public String getBlogID() {
		return this.blogID;
	}

	public void setBlogID(String blogID) {
		this.blogID = blogID;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public byte getDraft() {
		return draft;
	}
	
	public void setDraft(byte draft) {
		this.draft = draft;
	}
	
	public boolean isDraft() {
		return draft == DRAFT;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Comment> getComments() {
		return this.comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Comment addComment(Comment comment) {
		getComments().add(comment);
		comment.setBlog(this);

		return comment;
	}

	public Comment removeComment(Comment comment) {
		getComments().remove(comment);
		comment.setBlog(null);

		return comment;
	}
	
	public List<File> getAttachedFiles(){
		return this.attachedFiles;
	}
	
	public void setAttachedFiles(List<File> files){
		this.attachedFiles=files;
	}
	
	public File addAttachedFile(File attachedFile) {
		getAttachedFiles().add(attachedFile);
		attachedFile.setBlog(this);
		return attachedFile;
	    }

	    public File removeAttachedFile(File attachedFile) {
		getAttachedFiles().remove(attachedFile);
		attachedFile.setBlog(null);

		return attachedFile;
	    }
	    
	    public File getThumbnail() {
			return thumbnail;
		}
	    
	    public void setThumbnail(File thumbnail) {
			this.thumbnail = thumbnail;
		}

		@Override
		public String toString() {
			return "Blog [date=" + date + ", title=" + title + ", draft=" + draft + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((blogID == null) ? 0 : blogID.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			boolean result = eq(obj);
			System.out.println("comparing: " + this + " and " + obj + " = " + result);
			return result;
		}
		
		public boolean eq(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Blog other = (Blog) obj;
			if (blogID == null) {
				if (other.blogID != null)
					return false;
			} else if (!blogID.equals(other.blogID))
				return false;
			return true;
		}

		@Override
		public int compareTo(Blog other) {
			if (other == null || other.getDate() == null || getDate() == null) {
				return 0;
			} else {
				return -getDate().compareTo(other.getDate());
			}
		}
		
}