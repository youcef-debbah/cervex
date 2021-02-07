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

package com.rhcloud.cervex_jsoftware95.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.ArticleState;
import com.rhcloud.cervex_jsoftware95.entities.Article_;
import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.entities.Demand_;
import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.entities.File_;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.exceptions.CannotDeleteDemand;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

/**
 *
 * @author youcef debbah
 */
@Stateless
public class ArticleManagerBean extends FileManagerBean implements ArticleManager {

	private static Logger log = Logger.getLogger(ArticleManagerBean.class);

	@PersistenceContext(unitName = "cervex")
	EntityManager em;

	@EJB
	UserManager userManager;

	@EJB
	BlogManager bm;

	public static void checkIntegrity(Demand demand) {
		Objects.requireNonNull(demand);
		if (demand.getTitle() == null || demand.getType() == null || demand.getDescription() == null) {
			throw new NullPointerException("Illegal demand: " + demand);
		}
		UserManagerBean.checkIntegrity(demand.getUser());
	}

	public static void checkIntegrity(Article article) {
		Objects.requireNonNull(article);
		if (article.getVersion() == null) {
			throw new NullPointerException("Illegal demand: " + article);
		}
		UserManagerBean.checkIntegrity(article.getUser());
		checkIntegrity(article.getDemand());
	}

	@Override
	public List<Article> getAllArticles() {
		try {
			log.debug("trying to retrieve all articles");
			List<Article> articlesList = em.createNamedQuery("Article.findAll", Article.class).getResultList();
			log.debug("all articles retrieved");

			return articlesList;

		} catch (Exception e) {
			throw new EJBException("Could not retrieve all articles", e);
		}
	}

	@Override
	public void addArticle(String username, String demandID, String version, int progress) {
		Objects.requireNonNull(username);
		Objects.requireNonNull(demandID);
		Objects.requireNonNull(version);

		try {
			Article article = new Article();

			User user = userManager.getUser(username);
			user.addArticle(article);

			Demand demand = getDemand(demandID);
			demand.setArticle(article);

			article.setUser(user);
			article.setDemand(demand);
			article.setVersion(version);
			article.setProgress(progress);

			em.persist(article);
			log.debug("article added: " + article);
		} catch (Exception e) {
			throw new EJBException("Could not add article: username=" + username + ", demandID=" + demandID, e);
		}
	}

	@Override
	public Article getArticle(String articleID) {
		Objects.requireNonNull(articleID);

		try {
			log.debug("trying to retrieve article: " + articleID);
			Article article = em.find(Article.class, articleID);
			log.debug("article retrieved: " + article);
			return article;
		} catch (Exception e) {
			throw new EJBException("Could not retrieve article: " + articleID);
		}
	}

	@Override
	public void deleteArticle(String articleID) {
		Objects.requireNonNull(articleID);

		try {
			log.debug("trying to delete article: " + articleID);
			em.remove(getArticle(articleID));
			log.debug("article deleted: article: " + articleID);
		} catch (Exception e) {
			throw new InformationSystemException("Could not delete article: " + articleID, e);
		}
	}

	@Override
	public void updateArticle(String articleID, String version, int progress) {
		Objects.requireNonNull(articleID);
		Objects.requireNonNull(version);

		Article article = null;

		try {
			log.debug("trying to find article to be updated: " + articleID);
			article = em.find(Article.class, articleID);
		} catch (Exception e) {
			throw new EJBException("Could not retrieve article to be updated: " + articleID, e);
		}

		if (article == null)
			throw new InformationSystemException("article Not Found: " + articleID);
		log.debug("trying to update article: " + article);

		try {
			article.setVersion(version);
			article.setProgress(progress);
			article.setLastUpdate(Util.getCurrentTime());
			article.setState(ArticleState.UPDATED_ARTICLE.getStateName());
			em.merge(article);
		} catch (Exception e) {
			throw new EJBException("Could not update article: " + article, e);
		}

		log.debug("article updated: " + article);
	}

	@Override
	public List<Demand> getAllDemands() {
		try {
			log.debug("trying to retrieve all demands");
			List<Demand> demandsList = em.createNamedQuery("Demand.findAll", Demand.class).getResultList();
			log.debug("all demands retrieved");

			return demandsList;

		} catch (Exception e) {
			throw new EJBException("Could not retrieve all demands", e);
		}
	}

	@Override
	public void sendDemand(String title, String type, String description, Collection<File> files) {
		Objects.requireNonNull(title);
		Objects.requireNonNull(type);
		Objects.requireNonNull(description);

		try {
			Demand demand = new Demand();

			User currentUser = userManager.getCurrentUser();
			currentUser.addDemand(demand);

			demand.setUser(currentUser);
			demand.setTitle(title);
			demand.setType(type);
			demand.setDescription(description);

			log.debug("trying to send: " + demand);
			em.persist(demand);
			log.debug("demand sent: " + demand);

			if (files != null) {
				for (File file : files) {
					demand.addAttachedFile(file);
					em.persist(file);
					log.debug("file saved: " + file);
				}
			}
		} catch (Exception e) {
			throw new EJBException("Could not send demand", e);
		}
	}

	@Override
	public Demand getDemand(String demandID) {
		Objects.requireNonNull(demandID);

		try {
			log.debug("trying to retrieve demand: " + demandID);
			Demand demand = em.find(Demand.class, demandID);
			log.debug("demand retrieved: " + demand);
			return demand;
		} catch (Exception e) {
			throw new EJBException("Could not retrieve demand: " + demandID, e);
		}
	}

	@Override
	public boolean deleteDemand(String demandID) {
		Objects.requireNonNull(demandID);
		boolean hasArticle = getDemand(demandID).getArticle() != null;
		try {
			if (hasArticle) {
				log.debug("trying to delete Article that is atached to this demand " + demandID);
				deleteArticle(getDemand(demandID).getArticle().getArticleID());
			}
			if (!getDemand(demandID).getAttachedFiles().isEmpty()) {
				log.debug("trying to delete files that is atached to this demand " + demandID);
				Demand demand = em.createNamedQuery("Demand.findAttachedFiles", Demand.class)
						.setParameter("demandID", demandID).getSingleResult();
				for (File file : demand.getAttachedFiles())
					bm.removeFile(file.getfileID());
			}
			log.debug("trying to delete demand: " + demandID);
			em.remove(getDemand(demandID));
			log.debug("demand deleted: " + demandID);
			return hasArticle;
		} catch (InformationSystemException e) {
			throw new InformationSystemException("One of Article or files not found or already deleted", e);
		} catch (Exception e) {
			throw new CannotDeleteDemand(
					(getDemand(demandID).getArticle() != null) ? getDemand(demandID).getArticle().getArticleID() : null,
					(getDemand(demandID).getAttachedFiles() != null)
							? em.createNamedQuery("File.findByDemand", String.class)
									.setParameter("demand", getDemand(demandID)).getResultList()
							: null);
		}
	}

	@Override
	public void deleteDemand(String demandID, boolean canDelete) {
		Objects.requireNonNull(demandID);
		Demand demand = null;
		List<String> filesID = null;

		try {
			demand = getDemand(demandID);
			filesID = em.createQuery("select f.fileID from File f where f.demand=:demand", String.class)
					.setParameter("demand", demand).getResultList();
		} catch (Exception e) {
			throw new InformationSystemException("Error while retreiving Demand or files", e);
		}

		boolean hasArtOrFiles = demand.getArticle() != null || !filesID.isEmpty();
		if (!canDelete && hasArtOrFiles) {
			throw new CannotDeleteDemand((demand.getArticle() != null) ? demand.getArticle().getArticleID() : null,
					filesID);
		}
		try {
			if (!filesID.isEmpty()) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaDelete<File> dFile = cb.createCriteriaDelete(File.class);
				Root<File> files = dFile.from(File.class);
				dFile.where(files.get(File_.fileID).in(filesID));
				em.createQuery(dFile).executeUpdate();
			}

			if (demand.getArticle() != null)
				deleteArticle(demand.getArticle().getArticleID());
			log.debug("trying to delete demand: " + demandID);
			em.remove(getDemand(demandID));
			log.debug("demand deleted: " + demandID);
		} catch (Exception e) {
			throw new EJBException("Error while Deleting Demand or its Article or files", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rhcloud.cervex_jsoftware95.beans.ArticleManager#getDemandsStatics()
	 */
	@Override
	public Map<String, Long> getDemandsStatics() {
		Map<String, Long> data = new HashMap<>();
		List<DemandStaticRecord> results = em.createNamedQuery("getDemandStaticRecord", DemandStaticRecord.class)
				.getResultList();
		for (DemandStaticRecord result : results) {
			data.put(result.getType(), result.getCount());
		}

		return data;
	}

	@Override
	public List<Demand> getDemands(String[] demandIDs) {
		Objects.requireNonNull(demandIDs);
		if (demandIDs.length < 1) {
			return new ArrayList<>();
		} else {
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Demand> cq = cb.createQuery(Demand.class);
				Root<Demand> demand = cq.from(Demand.class);
				cq.select(demand).where(demand.get(Demand_.demandID).in(Arrays.asList(demandIDs)));
				return em.createQuery(cq).getResultList();
			} catch (Exception e) {
				throw new EJBException("Error while retrieving Demands of :" + demandIDs, e);
			}
		}
	}

	@Override
	public List<Article> getArticles(String[] articleIDs) {
		Objects.requireNonNull(articleIDs);
		if (articleIDs.length < 1) {
			return new ArrayList<>();
		} else {
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Article> cq = cb.createQuery(Article.class);
				Root<Article> article = cq.from(Article.class);
				cq.select(article).where(article.get(Article_.articleID).in(Arrays.asList(articleIDs)));
				return em.createQuery(cq).getResultList();
			} catch (Exception e) {
				throw new EJBException("Error while retrieving Articles of :" + articleIDs, e);
			}
		}
	}

	@Override
	public void markAsOldDemand(String demandID) {
		Objects.requireNonNull(demandID);
		try {
			Demand demand = getDemand(demandID);
			if (demand.isNew()) {
				demand.setState(Demand.OLD_DEMAND);
				em.merge(demand);
			}
		} catch (Exception e) {
			throw new EJBException("Error while Marking Demand as OLD :" + demandID, e);
		}
	}

	@Override
	public void markAsOldArticle(String articleID) {
		Objects.requireNonNull(articleID);
		try {
			Article article = getArticle(articleID);
			article.setState(ArticleState.OLD_ARTICLE.getStateName());
			em.merge(article);
		} catch (Exception e) {
			throw new EJBException("Error while Marking Article as OLD :" + articleID, e);
		}
	}

}
