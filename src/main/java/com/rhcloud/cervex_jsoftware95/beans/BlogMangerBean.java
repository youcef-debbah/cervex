package com.rhcloud.cervex_jsoftware95.beans;

import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.entities.Blog;
import com.rhcloud.cervex_jsoftware95.entities.Comment;
import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

@Stateless
public class BlogMangerBean extends FileManagerBean implements BlogManager {
	private static Logger log = Logger.getLogger(BlogMangerBean.class);

	@PersistenceContext(unitName = "cervex")
	EntityManager em;

	@EJB
	UserManager um;

	public static void checkIntegrity(Blog blog) {
		Objects.requireNonNull(blog);
		if (blog.getTitle() == null || blog.getIntro() == null) {
			throw new NullPointerException("Illegal blog: " + blog);
		}
		UserManagerBean.checkIntegrity(blog.getUser());
	}

	public static void checkIntegrity(Comment comment) {
		Objects.requireNonNull(comment);
		if (comment.getContent() == null)
			throw new NullPointerException("Illegal comment: " + comment);
		checkIntegrity(comment.getBlog());
		UserManagerBean.checkIntegrity(comment.getUser());
	}

	@Override
	public Blog getBlog(String blogID) {
		try {
			Objects.requireNonNull(blogID);
			log.debug("trying to retreive blog with ID :" + blogID);
			Blog b = em.find(Blog.class, blogID);
			if (b != null)
				return b;
			else
				throw new InformationSystemException("Can't find Blog with ID :" + blogID);
		} catch (NullPointerException e) {
			throw new NullPointerException("blogID is null");
		} catch (InformationSystemException e) {
			throw e;
		} catch (Exception e) {
			throw new EJBException("Can't retreive the blog :" + blogID, e);
		}
	}

	@Override
	public Blog getBlogWithComments(String blogID) {
		Blog result = getBlog(blogID);
		List<Comment> comments = result.getComments();
		if (comments != null) {
			int count = comments.size();
			log.info("comments loaded for blog: " + blogID + " = " + count);
		}
		return result;
	}

	@Override
	public Blog getDraft(String blogID) {
		Blog draft = getBlog(blogID);
		if (!draft.isDraft()) {
			draft.setDraft(Blog.DRAFT);
			em.merge(draft);
		}

		List<File> files = draft.getAttachedFiles();
		if (files != null) {
			int count = files.size();
			log.info("files loaded for blog: " + blogID + " = " + count);
		}
		return draft;
	}

	@Override
	public Comment getComment(String commentID) {
		try {
			Objects.requireNonNull(commentID);
			log.debug("trying to retreive comment with ID :" + commentID);
			Comment c = em.find(Comment.class, commentID);
			if (c != null)
				return c;
			else
				throw new InformationSystemException("Can't find comment with ID :" + commentID);
		} catch (NullPointerException e) {
			throw new NullPointerException("commentID is null");
		} catch (InformationSystemException e) {
			throw e;
		} catch (Exception e) {
			throw new EJBException("Can't retreive the comment :" + commentID, e);
		}
	}

	@Override
	public void authorizeComment(String commentID) {
		Objects.requireNonNull(commentID);

		try {
			Comment comment = getComment(commentID);
			if (comment.isNew()) {
				comment.setAuthorized(Comment.AUTHORIZED_COMMENT);
				em.merge(comment);
			}
		} catch (Exception e) {
			throw new EJBException("Error while Marking comment as authorized :" + commentID, e);
		}
	}

	@Override
	public String createBlog(String blogTitle, String blogIntro, String blogContent, File thumbnail, boolean isDraft) {
		try {
			log.debug("checking parametres");
			Objects.requireNonNull(blogTitle);
			Objects.requireNonNull(blogIntro);

			Blog blog = new Blog();
			blog.setTitle(blogTitle);
			blog.setIntro(blogIntro);
			blog.setContent(blogContent);
			blog.setUser(um.getCurrentUser());
			blog.setDraft(isDraft ? Blog.DRAFT : Blog.POSTED);

			if (thumbnail != null) {
				thumbnail.setBlog(null);
				thumbnail.setDemand(null);
				em.persist(thumbnail);
				blog.setThumbnail(thumbnail);
			}

			log.debug("trying to persist the blog " + blog);
			em.persist(blog);
			log.debug(blog + " is created ");
			return blog.getBlogID();
		} catch (NullPointerException e) {
			throw e;
		} catch (EntityExistsException e) {
			throw new InformationSystemException("The blog already exist", e);
		} catch (Exception e) {
			throw new EJBException("Can't Create blog", e);
		}

	}

	@Override
	public void updateBlog(Blog blog) {
		try {
			checkIntegrity(blog);
			Blog oldBlog = getBlog(blog.getBlogID());
			if (oldBlog.isDraft() && !blog.isDraft()) {
				blog.setDate(Util.getCurrentTime());
			}
			em.merge(blog);
		} catch (NullPointerException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw new InformationSystemException(blog + " is not an entity or is deleted", e);
		} catch (Exception e) {
			throw new EJBException("Can't update blog :" + blog, e);
		}
	}

	@Override
	public void deleteBlog(String blogID) {
		try {
			Objects.requireNonNull(blogID);
			Blog b = em.find(Blog.class, blogID);
			if (!b.getComments().isEmpty()) {
				log.debug("trying to get blog's attached comments with ID :" + blogID);
				Blog blog = em.createNamedQuery("Blog.findFetchedComments", Blog.class).setParameter("blogID", blogID)
						.getSingleResult();
				log.debug("trying to delete Blog's comments");
				for (Comment c : blog.getComments())
					removeComment(c.getCommentID());
			}
			if (!b.getAttachedFiles().isEmpty()) {
				log.debug("trying to get blog's attached files with ID :" + blogID);
				Blog blog2 = em.createNamedQuery("Blog.findFetchedFiles", Blog.class).setParameter("blogID", blogID)
						.getSingleResult();
				log.debug("trying to delete Blog's files");
				for (File f : blog2.getAttachedFiles())
					removeFile(f.getfileID());
			}

			File thumbnail = b.getThumbnail();
			if (thumbnail != null) {
				b.setThumbnail(null);
				em.merge(b);
				em.remove(thumbnail);
			}

			em.remove(b);
		} catch (NullPointerException e) {
			throw new NullPointerException("the given ID was null");
		} catch (NoResultException e) {
			throw e;
		} catch (InformationSystemException e) {
			throw new InformationSystemException("Error happened during deleting Blog's contents", e);
		} catch (Exception e) {
			throw new EJBException("Somthing happened during deleting blog " + blogID, e);
		}
	}

	@Override
	public String addComment(String content, String blogID, String username, boolean needValidation) {
		try {
			log.debug("checking parametres");
			Objects.requireNonNull(content);
			Objects.requireNonNull(blogID);
			log.debug("findind blog with ID of :" + blogID);
			Blog blog = getBlog(blogID);

			User user = null;
			if (username != null) {
				log.debug("finding user with username :" + username);
				user = um.getUser(username);
			}

			log.debug("Creating new Comment");
			Comment comment = new Comment();
			comment.setContent(content);
			comment.setBlog(blog);
			comment.setUser(user);
			comment.setAuthorized(needValidation ? Comment.NEW_COMMENT : Comment.AUTHORIZED_COMMENT);
			em.persist(comment);
			log.debug(comment + " was successfully persisted");
			return comment.getCommentID();
		} catch (NullPointerException e) {
			throw new NullPointerException("One of parametres are Null");
		} catch (InformationSystemException e) {
			throw new InformationSystemException(
					String.format("Blog with ID of [%s] or User with username [%s] not found", blogID, username), e);
		} catch (Exception e) {
			throw new EJBException("Couldn't create new Comment", e);
		}
	}

	@Override
	public void removeComment(String commentID) {
		try {
			Objects.requireNonNull(commentID);
			Comment comment = em.find(Comment.class, commentID);
			if (comment != null)
				em.remove(comment);
			else
				throw new InformationSystemException("the comment with ID :" + commentID + " not found");

		} catch (NullPointerException e) {
			throw new NullPointerException("the given ID was null");
		} catch (InformationSystemException e) {
			throw e;
		} catch (Exception e) {
			throw new EJBException("Somthing happened during deleting comment " + commentID, e);
		}
	}

	@Override
	public File addFile(String filename, String fileType, long fileSize, byte[] fileContent, String blogID) {
		try {
			Objects.requireNonNull(blogID);
			File file = getFile(filename, fileType, fileSize, fileContent);

			log.debug("findind blog with ID of :" + blogID);
			Blog blog = getBlog(blogID);
			file.setBlog(blog);
			log.debug("persisting file :" + file);
			em.persist(file);
			log.debug(file + " was successfully persisted");
			return file;
		} catch (InformationSystemException e) {
			throw new InformationSystemException(String.format("Blog with ID of [%s] not found", blogID), e);
		} catch (Exception e) {
			throw new EJBException("Couldn't create new File", e);
		}
	}

	private File getFile(String filename, String fileType, long fileSize, byte[] fileContent) {
		Objects.requireNonNull(filename);
		Objects.requireNonNull(fileType);
		Objects.requireNonNull(fileSize);
		Objects.requireNonNull(fileContent);

		log.debug("Creating new File");
		File file = new File();
		file.setName(filename);
		file.setContentType(fileType);
		file.setSize(fileSize);
		file.setContents(fileContent);

		return file;
	}

	@Override
	public File setThumbnail(String filename, String fileType, long fileSize, byte[] fileContent, String blogID) {
		try {
			Objects.requireNonNull(blogID);
			File file = getFile(filename, fileType, fileSize, fileContent);

			log.debug("persisting file :" + file);
			em.persist(file);
			log.debug(file + " was successfully persisted");

			log.debug("findind blog with ID of :" + blogID);
			Blog blog = getBlog(blogID);
			blog.setThumbnail(file);
			updateBlog(blog);

			return file;
		} catch (Exception e) {
			throw new EJBException("cannot set thumbnail on blog: " + blogID, e);
		}
	}

	@Override
	public List<Blog> getAllBlogs() {
		try {
			return em.createNamedQuery("Blog.findAll", Blog.class).getResultList();
		} catch (Exception e) {
			throw new EJBException("Error during retreiving Blogs", e);
		}
	}

	@Override
	public List<Comment> getAllComments() {
		try {
			return em.createNamedQuery("Comment.findAll", Comment.class).getResultList();
		} catch (Exception e) {
			throw new EJBException("Error during retreiving Comments", e);
		}
	}

	@Override
	public void removeThumbnail(String blogID) {
		Objects.requireNonNull(blogID);

		try {
			Blog blog = getBlog(blogID);
			File thumbnail = blog.getThumbnail();
			if (thumbnail != null) {
				blog.setThumbnail(null);
				em.merge(blog);
				em.remove(thumbnail);
			}
		} catch (Exception e) {
			throw new EJBException(e);
		}

	}
}
