package com.rhcloud.cervex_jsoftware95.beans;

import com.google.common.truth.Truth;
import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.ejb.EJBException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;

public class ArticleManagerBeanTest {

    @Mock
    private EntityManager em;

    @Mock
    private UserManager userManager;

    @Mock
    private BlogManager bm;

    private ArticleManagerBean articleManagerBean;

    @Before
    public void setUp() {
        articleManagerBean = new ArticleManagerBean();
        articleManagerBean.em = em;
        articleManagerBean.userManager = userManager;
        articleManagerBean.bm = bm;
    }

    @Test
    public void testGetAllArticles_success() {
        List<Article> articles = new ArrayList<>();
        Mockito.when(em.createNamedQuery("Article.findAll", Article.class)).thenReturn((TypedQuery<Article>) Mockito.mock(Query.class));
        Mockito.when(Mockito.mock(javax.persistence.Query.class).getResultList()).thenReturn(articles);

        List<Article> retrievedArticles = articleManagerBean.getAllArticles();
        Truth.assertThat(retrievedArticles).isEqualTo(articles);
    }

    @Test
    public void testGetAllArticles_throwsException() {
        Mockito.when(em.createNamedQuery("Article.findAll", Article.class)).thenThrow(new RuntimeException());

        assertThrows(EJBException.class, () -> articleManagerBean.getAllArticles());
    }

    @Test(expected = NullPointerException.class)
    public void testAddArticle_nullUsername() {
        articleManagerBean.addArticle(null, "demandID", "version", 10);
    }

    @Test(expected = NullPointerException.class)
    public void testAddArticle_nullDemandID() {
        articleManagerBean.addArticle("username", null, "version", 10);
    }

    @Test(expected = NullPointerException.class)
    public void testAddArticle_nullVersion() {
        articleManagerBean.addArticle("username", "demandID", null, 10);
    }

    @Test
    public void testAddArticle_success() throws Exception {
        String username = "test_user";
        String demandID = "demand_id";
        String version = "v1";
        int progress = 10;

        User user = Mockito.mock(User.class);

        Mockito.when(userManager.getUser(username)).thenReturn(user);

        articleManagerBean.addArticle(username, demandID, version, progress);

        Mockito.verify(user).addArticle(Mockito.any(Article.class));
        Mockito.verify(em).persist(Mockito.any(Article.class));
    }

    @Test
    public void testGetArticle_success() {
        String articleID = "article_id";
        Article article = Mockito.mock(Article.class);

        Mockito.when(em.find(Article.class, articleID)).thenReturn(article);

        Article retrievedArticle = articleManagerBean.getArticle(articleID);

        Truth.assertThat(retrievedArticle).isEqualTo(article);
        Mockito.verify(em).find(Article.class, articleID);
    }
}
