package com.rhcloud.cervex_jsoftware95.beans;

import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.ArticleState;
import com.rhcloud.cervex_jsoftware95.entities.Comment;
import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.entities.Message;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

@Stateless
public class NotificationManagerBean implements NotificationManager {
	@PersistenceContext(unitName = "cervex")
	EntityManager em;
	@EJB
	UserManager um;

	@Override
	public List<Demand> getNewDemands() {
		try {
			return em.createQuery("Select d from Demand d where d.state=:state", Demand.class)
					.setParameter("state", Demand.NEW_DEMAND).getResultList();
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Demands", e);
		}
	}
	
	@Override
	public List<Comment> getNewComments() {
		try {
			return em.createQuery("Select c from Comment c where c.authorized=:state", Comment.class)
					.setParameter("state", Comment.NEW_COMMENT).getResultList();
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Comments", e);
		}
	}

	@Override
	public List<Article> getNewArticles() {
		try {
			return em.createQuery("Select a from Article a where a.state=:state", Article.class)
					.setParameter("state", ArticleState.NEW_ARTICLE.toString()).getResultList();
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Articles", e);
		}
	}

	@Override
	public List<Article> getUpdatedArticles() {
		try {
			return em.createQuery("Select a from Article a where a.state=:state", Article.class)
					.setParameter("state", ArticleState.UPDATED_ARTICLE.toString()).getResultList();
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Articles", e);
		}
	}

	@Override
	public List<Message> getNewReceivedMessages() {
		try {
			return em.createQuery("Select m from Message m where m.state=:state", Message.class)
					.setParameter("state", Message.NEW_MESSAGE).getResultList();
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Messages");
		}
	}

	@Override
	public List<Demand> getNewDemands(String username) {
		try {
			Objects.requireNonNull(username);
			User user = um.getUser(username);
			return em.createQuery("Select d from Demand d where d.user=:user and d.state=:state", Demand.class)
					.setParameter("user", user).setParameter("state", Demand.NEW_DEMAND).getResultList();
		} catch (NullPointerException e) {
			throw e;
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Demands", e);
		}
	}

	@Override
	public List<Article> getNewArticles(String username) {
		try {
			Objects.requireNonNull(username);
			User user = um.getUser(username);
			return em.createQuery("Select a from Article a where a.user=:user and a.state=:state", Article.class)
					.setParameter("user", user).setParameter("state", ArticleState.NEW_ARTICLE.toString())
					.getResultList();
		} catch (NullPointerException e) {
			throw e;
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Articles", e);
		}
	}

	@Override
	public List<Article> getUpdatedArticles(String username) {
		try {
			Objects.requireNonNull(username);
			User user = um.getUser(username);
			return em.createQuery("Select a from Article a where a.user=:user and a.state=:state", Article.class)
					.setParameter("user", user).setParameter("state", ArticleState.UPDATED_ARTICLE.toString())
					.getResultList();
		} catch (NullPointerException e) {
			throw e;
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Articles", e);
		}
	}

	@Override
	public List<Message> getNewReceivedMessages(String username) {
		try {
			Objects.requireNonNull(username);
			User user = um.getUser(username);
			return em.createQuery("Select m from Message m where m.receiver=:user and m.state=:state", Message.class)
					.setParameter("user", user).setParameter("state", Message.NEW_MESSAGE).getResultList();
		} catch (NullPointerException e) {
			throw e;
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Messages", e);
		}
	}
}
