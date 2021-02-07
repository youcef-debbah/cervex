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
 * created on 2018/01/06
 * @header
 */

package com.rhcloud.cervex_jsoftware95.view;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.beans.BlogManager;
import com.rhcloud.cervex_jsoftware95.control.Meta;
import com.rhcloud.cervex_jsoftware95.control.PageServer;
import com.rhcloud.cervex_jsoftware95.entities.Blog;
import com.rhcloud.cervex_jsoftware95.util.WebKit;

/**
 * 
 * @author youcef debbah
 */
@Named
@ViewScoped
public class PostsView implements Serializable {

	private static final long serialVersionUID = 5536624951736181578L;
	private static final Logger log = Logger.getLogger(PostsView.class);

	private List<Blog> posts;
	private List<Blog> drafts;

	@EJB
	BlogManager blogManager;

	@PostConstruct
	private void init() {
		List<Blog> data = blogManager.getAllBlogs();
		posts = new ArrayList<>(data.size());
		drafts = new ArrayList<>(data.size());

		for (int i = data.size() - 1; i >= 0; i--) {
			Blog blog = data.get(i);
			if (!blog.isDraft())
				posts.add(blog);
			else
				drafts.add(blog);
		}

		Collections.sort(posts);
		Collections.sort(drafts);
	}

	public List<Blog> getPosts() {
		return posts;
	}

	public void delete(String id) {
		if (id == null)
			return;

		try {
			blogManager.deleteBlog(id);
			if (posts != null)
				posts.removeIf(blog -> id.equals(blog.getBlogID()));
			if (drafts != null)
				drafts.removeIf(blog -> id.equals(blog.getBlogID()));
		} catch (Exception e) {
			Meta.handleInternalError(e);
		}
	}

	public List<Blog> getDrafts() {
		return drafts;
	}

	public String getIntroURI(String blogID) {
		if (blogID == null)
			return null;

		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put(PageServer.BLOG_INTRO, blogID);
			try {
				parameters.put(PageServer.LANG,
						FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage());
			} catch (Exception e) {
				log.error("could not get locale from view root", e);
			}
			return WebKit.buildURI(PageServer.PAGE_SERVER_PATTERN, parameters);
		} catch (URISyntaxException e) {
			log.error("cannot build poster URI for blog: " + blogID);
			return null;
		}
	}

}
