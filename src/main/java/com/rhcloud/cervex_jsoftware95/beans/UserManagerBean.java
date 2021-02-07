
package com.rhcloud.cervex_jsoftware95.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.Article_;
import com.rhcloud.cervex_jsoftware95.entities.Comment;
import com.rhcloud.cervex_jsoftware95.entities.Comment_;
import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.entities.File_;
import com.rhcloud.cervex_jsoftware95.entities.Message;
import com.rhcloud.cervex_jsoftware95.entities.Message_;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.exceptions.CannotDeleteUser;
import com.rhcloud.cervex_jsoftware95.exceptions.EmailExistException;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;
import com.rhcloud.cervex_jsoftware95.exceptions.UsernameExistException;

/**
 * @author KratosPOP
 *
 */
@Stateless
public class UserManagerBean implements UserManager {

	private static Logger log = Logger.getLogger(UserManagerBean.class);

	@PersistenceContext(unitName = "cervex")
	EntityManager em;
	@Resource
	SessionContext ctx;
	@EJB
	ArticleManager amb;

	/**
	 * Checks if the given user is not missing any property.
	 *
	 * @param user the User you want to validate its argument
	 * @return true if its important properties are not null
	 */
	public static void checkIntegrity(User user) {
		Objects.requireNonNull(user);
		if (user.getUsername() == null || user.getPassword() == null || user.getRole() == null
				|| user.getPhoneNumber() == null || user.getEmail() == null) {
			throw new NullPointerException("Illegal user: " + user);
		}
	}

	@Override
	public List<ArticleApply> getAllApplies() {
		try {
			log.debug("trying to retrieve all applies");
			List<ArticleApply> applies = new ArrayList<>();
			List<User> users = getAllUsers();

			for (User user : users) {
				for (Demand demand : user.getDemands()) {
					applies.add(new ArticleApplyRecord(user, demand, demand.getArticle()));
				}
			}

			log.debug("all applies rerieved: " + applies);
			return applies;
		} catch (Exception e) {
			throw new EJBException("Could not retrieve all applies", e);
		}
	}

	@Override
	public List<ArticleApply> getApplies(String username) {
		Objects.requireNonNull(username);

		try {
			log.debug("trying to retrieve applies for user: " + username);
			List<ArticleApply> applies = new ArrayList<>();
			User user = getUser(username);

			for (Demand demand : user.getDemands()) {
				applies.add(new ArticleApplyRecord(user, demand, demand.getArticle()));
			}

			log.debug("applies for user: " + username + ", retrieved: " + applies);
			return applies;
		} catch (Exception e) {
			throw new EJBException("Could not retrieve all applies for user: ", e);
		}
	}

	@Override
	public List<User> getAllUsers() {
		try {
			log.debug("trying to retrieve all users");
			List<User> usersList = em.createNamedQuery("User.findAll", User.class).getResultList();
			log.debug("all users retrieved");
			return usersList;

		} catch (Exception e) {
			throw new EJBException("Could not retrieve all users", e);
		}
	}

	@Override
	public User getUser(String username) {
		Objects.requireNonNull(username);

		try {
			log.debug("trying to retrieve current user: " + username);
			User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username)
					.getSingleResult();
			log.debug("current user retrieved: " + user);
			return user;
		} catch (NoResultException e) {
			throw new InformationSystemException("User Not Found", e);
		} catch (Exception e) {
			throw new EJBException("Could not retrieve user with username: " + username, e);
		}
	}

	@Override
	public User getCurrentUser() {
		return getUser(ctx.getCallerPrincipal().getName());
	}

	@Override
	public void create(User newUser) {
		try {
			log.debug("Trying to check User's arguments ");
			checkIntegrity(newUser);
			log.debug("checking existing of the same username :" + newUser.getUsername());
			if (usernameExist(newUser.getUsername()))
				throw new UsernameExistException(newUser.getUsername());
			log.debug("checking existing of same email :" + newUser.getEmail());
			if (emailExist(newUser.getEmail()))
				throw new EmailExistException(newUser.getEmail());
			log.debug("trying to create: " + newUser);
			em.persist(newUser);
			log.debug("user created: " + newUser);
		} catch (NullPointerException a) {
			throw new NullPointerException(String.format("One of %s arguments is NULL", newUser));
		} catch (UsernameExistException e) {
			throw new UsernameExistException(newUser.getUsername());
		} catch (EmailExistException e3) {
			throw new EmailExistException(newUser.getEmail());
		} catch (EntityExistsException e1) {
			throw new InformationSystemException("user already exist: " + newUser, e1);
		} catch (Exception e2) {
			throw new EJBException("Could not create user: " + newUser, e2);
		}
	}

	@Override
	public void updateUser(User newState) {
		checkIntegrity(newState);

		User oldState = null;

		try {
			log.debug("trying to find user to be updated: " + newState);
			oldState = em.find(User.class, newState.getUserID());
		} catch (Exception e) {
			throw new EJBException("Error while trying to find user: " + newState, e);
		}

		if (oldState == null)
			throw new InformationSystemException("user Not Found: " + newState);

		try {
			newState.setCreationDate(oldState.getCreationDate());
			log.debug("trying to update user");
			em.merge(newState);
		} catch (Exception e) {
			throw new EJBException("Could not update user: " + newState, e);
		}

		log.debug("user updated: " + newState);
	}

	@Override
	public void delete(String username, boolean deleteComment, boolean canDelete) {
		Objects.requireNonNull(username, "Username must not be NULL");

		User user;
		try {
			user = getUser(username);
		} catch (Exception e) {
			throw new EJBException("No user with username " + username + " was found");
		}

		List<String> demands = getResultList("Demand.findByUser", user);
		boolean hasDemands = demands != null && !demands.isEmpty();

		List<String> articles = getResultList("Article.findByUser", user);
		boolean hasArticles = articles != null && !articles.isEmpty();

		List<String> receivedMessages = getResultList("Message.findByUserRec", user);
		boolean hasReceivedMessages = receivedMessages != null && !receivedMessages.isEmpty();

		List<String> sentMessages = getResultList("Message.findByUserSen", user);
		boolean hasSentMessages = sentMessages != null && !sentMessages.isEmpty();

		if (!canDelete)
			if (hasDemands || hasArticles || hasReceivedMessages || hasSentMessages)
				throw new CannotDeleteUser(demands, articles, receivedMessages, sentMessages);

		try {
			if (deleteComment) {
				deleteEntities(Comment.class, (crit, root) -> crit.equal(root.get(Comment_.user), user));
			} else {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<Comment> comment = cb.createCriteriaUpdate(Comment.class);
				Root<Comment> e = comment.from(Comment.class);
				comment.set(Comment_.user, (User) null);
				comment.where(cb.equal(e.get(Comment_.user), user));
				em.createQuery(comment).executeUpdate();
			}

			if (hasDemands) {
				List<Demand> demandsEntities = new ArrayList<>(demands.size());
				demands.forEach(demand -> demandsEntities.add(amb.getDemand(demand)));

				deleteEntities(File.class, (cb, file) -> file.get(File_.demand).in(demandsEntities));

				if (hasArticles)
					deleteEntities(Article.class, (cb, article) -> article.get(Article_.demand).in(demandsEntities));

				deleteEntities(Demand.class, (cb, demand) -> demand.get("demandID").in(demands));
			}
			if (hasReceivedMessages || hasSentMessages) {
				deleteEntities(Message.class, (cb, m) -> cb.or(cb.equal(m.get(Message_.sender), user),
						cb.equal(m.get(Message_.receiver), user)));
			}

			log.debug("Delete User :" + username);
			em.remove(user);
			log.debug(username + " was deleted: ");
		} catch (Exception e) {
			throw new EJBException("error while deleting user: " + username, e);
		}
	}

	private List<String> getResultList(String namedQuery, User user) {
		try {
			return em.createNamedQuery(namedQuery, String.class).setParameter("user", user).getResultList();
		} catch (Exception e) {
			throw new EJBException();
		}
	}

	private <T> void deleteEntities(Class<T> type, BiFunction<CriteriaBuilder, Root<T>, Predicate> getPredicate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<T> cd = cb.createCriteriaDelete(type);
		Root<T> root = cd.from(type);
		cd.where(getPredicate.apply(cb, root));
		em.createQuery(cd).executeUpdate();
	}

	private boolean usernameExist(String username) {
		Objects.requireNonNull(username);

		if ("anonymous".equalsIgnoreCase(username))
			return true;

		try {
			log.debug("trying to retrieve username: " + username);
			String result = em.createNamedQuery("User.findUsername", String.class).setParameter("username", username)
					.getSingleResult();

			log.debug("username retrieved: " + result);
			return true;
		} catch (NoResultException e) {
			log.debug("username not found");
			return false;
		} catch (Exception e) {
			throw new EJBException("Could not retrieve username: " + username, e);
		}
	}

	private boolean emailExist(String email) {
		Objects.requireNonNull(email);

		try {
			log.debug("trying to retrieve email: " + email);
			String result = em.createNamedQuery("User.findEmail", String.class).setParameter("email", email)
					.getSingleResult();

			log.debug("email retrieved: " + result);
			return true;
		} catch (NoResultException e) {
			log.debug("email not found");
			return false;
		} catch (Exception e) {
			throw new EJBException("Could not retrieve email: " + email, e);
		}
	}

}
