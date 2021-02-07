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
 * created on 2018/12/30
 * @header
 */

package com.rhcloud.cervex_jsoftware95.beans;

import java.io.Serializable;
import java.util.Objects;

import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.entities.User;

/**
 * @author youcef debbah
 */
public class ArticleApplyRecord implements ArticleApply, Serializable {

    private static final long serialVersionUID = 5927756606135455844L;

    private User user;
    private Demand demand;
    private Article article;

    public ArticleApplyRecord(User user, Demand demand, Article article) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(demand);

        this.user = user;
        this.demand = demand;
        this.article = article;
    }

    @Override
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    @Override
    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    @Override
    public String toString() {
        return "ArticleApplyRecord [user=" + user.getUsername() + ", demand=" + demand + ", article=" + article + "]";
    }

}
