package com.rhcloud.cervex_jsoftware95.beans;

import com.google.common.truth.Truth;
import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.entities.User;
import org.junit.Test;

public class ArticleApplyRecordTest {

    @Test
    public void testArticleApplyRecordConstruction() {
        User user = new User();
        user.setUsername("test_user");
        Demand demand = new Demand();
        Article article = new Article();

        ArticleApplyRecord record = new ArticleApplyRecord(user, demand, article);

        Truth.assertThat(record.getUser()).isEqualTo(user);
        Truth.assertThat(record.getDemand()).isEqualTo(demand);
        Truth.assertThat(record.getArticle()).isEqualTo(article);
    }

    @Test(expected = NullPointerException.class)
    public void testNullUserConstruction() {
        Demand demand = new Demand();
        Article article = new Article();

        new ArticleApplyRecord(null, demand, article);
    }

    @Test(expected = NullPointerException.class)
    public void testNullDemandConstruction() {
        User user = new User();
        user.setUsername("test_user");
        Article article = new Article();

        new ArticleApplyRecord(user, null, article);
    }

    @Test
    public void testSettersAndGetters() {
        User user = new User();
        user.setUsername("test_user");
        Demand demand = new Demand();
        Article article = new Article();

        ArticleApplyRecord record = new ArticleApplyRecord(user, demand, article);

        User newUser = new User();
        newUser.setUsername("new_user");
        Demand newDemand = new Demand();
        Article newArticle = new Article();

        record.setUser(newUser);
        record.setDemand(newDemand);
        record.setArticle(newArticle);

        Truth.assertThat(record.getUser()).isEqualTo(newUser);
        Truth.assertThat(record.getDemand()).isEqualTo(newDemand);
        Truth.assertThat(record.getArticle()).isEqualTo(newArticle);
    }

    @Test
    public void testToString() {
        User user = new User();
        user.setUsername("test_user");
        Demand demand = new Demand();
        Article article = new Article();

        ArticleApplyRecord record = new ArticleApplyRecord(user, demand, article);

        String expectedString = "ArticleApplyRecord [user=" + user.getUsername() + ", demand=" + demand + ", article=" + article + "]";
        Truth.assertThat(record.toString()).isEqualTo(expectedString);
    }
}