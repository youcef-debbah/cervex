package com.rhcloud.cervex_jsoftware95.control;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.beans.BlogManager;
import com.rhcloud.cervex_jsoftware95.entities.Blog;
import com.rhcloud.cervex_jsoftware95.entities.Comment;

@WebServlet(urlPatterns = PageServer.PAGE_SERVER_PATTERN)
public class PageServer extends HttpServlet {
	private static final long serialVersionUID = -7422113949422585529L;
	private static final Logger log = Logger.getLogger(PageServer.class);

	public static final String PAGE_SERVER_PATTERN = "/page";
	public static final String BLOG = "blog";
	public static final String BLOG_INTRO = "blogIntro";
	public static final String LANG = "lang";
	public static final String COMMENT = "comment";

	@EJB
	private BlogManager blogManager;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pageContent = "";
		String blogID;

		blogID = request.getParameter(BLOG_INTRO);
		if (blogID != null) {
			try {
				Blog blog = blogManager.getBlog(blogID);
				pageContent = blog.getIntro();
			} catch (Exception e) {
				log.error(e);
			}
		} else {
			blogID = request.getParameter(BLOG);
			if (blogID != null) {
				try {
					Blog blog = blogManager.getBlog(blogID);
					pageContent = blog.getContent();
				} catch (Exception e) {
					log.error(e);
				}
			} else {
				String commentID = request.getParameter(COMMENT);
				if (commentID != null) {
					Comment comment = blogManager.getComment(commentID);
					pageContent = comment.getContent();
				}
			}
		}

		PrintWriter writer = response.getWriter();
		String path = request.getContextPath();
		writer.write(getPage(path, pageContent, request.getParameter(LANG)));
	}

	private String getPage(String contextPath, String pageContent, String lang) {
		if (contextPath == null) {
			contextPath = "";
		}

		if (pageContent == null)
			pageContent = "";

		if (lang == null || lang.isEmpty())
			lang = "en";

		return "<!DOCTYPE html><html lang=\"" + lang
				+ "\"><head><title>Blog</title><script type=\"text/javascript\" src=\"" + contextPath
				+ "/javax.faces.resource/iframeResizer.contentWindow.min.js.xhtml?ln=js&v=1_0\"></script></head><body>" + pageContent
				+ "</body></html>";
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
