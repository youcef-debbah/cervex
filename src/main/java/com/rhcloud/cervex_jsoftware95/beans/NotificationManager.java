package com.rhcloud.cervex_jsoftware95.beans;

import java.util.List;

import javax.ejb.Local;

import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.Comment;
import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.entities.Message;

@Local
public interface NotificationManager {

	public List<Demand> getNewDemands();
	
	public List<Comment> getNewComments();

	public List<Article> getNewArticles();

	public List<Article> getUpdatedArticles();

	public List<Message> getNewReceivedMessages();

	/*
	 * for admin
	 */
	public List<Demand> getNewDemands(String username);
	
	/*
	 * for user
	 */
	public List<Article> getNewArticles(String username);

	/*
	 * for user
	 */
	public List<Article> getUpdatedArticles(String username);

	/*
	 * for all
	 */
	public List<Message> getNewReceivedMessages(String username);

}
