package com.rhcloud.cervex_jsoftware95.beans;

import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.rhcloud.cervex_jsoftware95.entities.Blog;
import com.rhcloud.cervex_jsoftware95.entities.Comment;
import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

@Local
public interface BlogManager extends FileManager {

	/**
	 * This methode retreive blog with the given {@code blogID}
	 * 
	 * @param blogID
	 *            the ID of blog you want to retreive
	 * @return Blog instance
	 * @throws NullPointerException
	 *             if {@code blogID} is null
	 * @throws InformationSystemException
	 *             if the blog not found
	 * @throws EJBException
	 *             if something happend during retreiving that blog
	 */
	public Blog getBlog(String blogID);
	
	public Blog getBlogWithComments(String blogID);
	
	public Blog getDraft(String draftID);

	/**
	 * This methode retreive comment with the given {@code commentID}
	 * 
	 * @param commentID
	 *            the ID of comment you want to retreive
	 * @return Comment instance
	 * @throws NullPointerException
	 *             if {@code commentID} is null
	 * @throws InformationSystemException
	 *             if the comment not found
	 * @throws EJBException
	 *             if something happend during retreiving that comment
	 */
	public Comment getComment(String commentID);

	/**
	 * @return
	 */
	public List<Blog> getAllBlogs();

	public List<Comment> getAllComments();

	/**
	 * This methode create a new blog with the given {@code blog} parameter
	 * 
	 * @param isDraft
	 * 
	 * @param linkedHashSet
	 * @param blog
	 *            The blog you want to create
	 * 
	 * @throws NullPointerException
	 *             if the {@code blog} is missed a parameter
	 * @throws InformationSystemException
	 *             if {@code blog} is already exist
	 * @throws EJBException
	 *             if some error happend during creation of {@code blog}
	 */
	public String createBlog(String blogTitle, String blogIntro, String blogContent, File thumbnail, boolean isDraft);

	/**
	 * This method update the {@code blog} passed in parameter in the
	 * persistance context
	 * 
	 * @param blog
	 *            the blog you want to update
	 * @throws NullPointerException
	 *             if the {@code blog} is missed a parameter
	 * @throws InformationSystemException
	 *             if {@code blog} is not an entity or is deleted
	 * @throws EJBException
	 *             if some error happend during update of {@code blog}
	 */
	public void updateBlog(Blog blog);

	/**
	 * This methode delete Blog and its comments and files from database
	 * 
	 * @param blogID
	 *            the ID of blog you to delete
	 * @throws NullPointerException
	 *             if {@code blogID} was Null
	 * @throws NoResultException
	 *             if the blog with {@code blogID} not found or already deleted
	 * @throws InformationSystemException
	 *             if error happened during deleting Blog's contents like files
	 *             and comments
	 * @throws EJBException
	 *             if error happened during removing blog
	 */
	public void deleteBlog(String blogID);

	/**
	 * This methode Create a new Comment and attache it to Blog and User
	 * 
	 * @param content
	 *            the body of the comment
	 * @param blogID
	 *            the ID of blog that comment belong
	 * @param userusername
	 *            the username of user that wrote this comment
	 * @param needValidation 
	 * @throws NullPointerException
	 *             if one of parametres are null
	 * @throws InformationSystemException
	 *             if the Blog or User that wrote comment not found
	 * @throws EJBException
	 *             if error happened during Creating Comment
	 */
	public String addComment(String content, String blogID, String userusername, boolean needValidation);

	/**
	 * This methode delete comment from database
	 * 
	 * @param commentID
	 *            the ID of comment you to delete
	 * @throws NullPointerException
	 *             if {@code commentID} was Null
	 * @throws InformationSystemException
	 *             if the comment with {@code commentID} not found or already
	 *             deleted
	 * @throws EJBException
	 *             if error happened during removing comment
	 */
	public void removeComment(String commentID);

	/**
	 * This methode Create a new File and attache it to Blog
	 * 
	 * @param filename
	 *            the name of the file
	 * @param fileType
	 *            the type of the file
	 * @param fileSize
	 *            the size of the file
	 * @param fileContent
	 *            the contents of the file
	 * @param blogID
	 *            the ID of blog that file attached to
	 * @throws NullPointerException
	 *             if one of parametres are null
	 * @throws InformationSystemException
	 *             if the Blog that relative to file not found
	 * @throws EJBException
	 *             if error happened during Creating File
	 */
	public File addFile(String filename, String fileType, long fileSize, byte[] fileContent, String blogID);
	
	public File setThumbnail(String filename, String fileType, long fileSize, byte[] fileContent, String blogID);

	public void authorizeComment(String commentID);

	public void removeThumbnail(String draftID);

}
