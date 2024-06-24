package com.rhcloud.cervex_jsoftware95.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-04-26T14:01:12.679+0100")
@StaticMetamodel(Article.class)
public class Article_ {
	public static volatile SingularAttribute<Article, String> articleID;
	public static volatile SingularAttribute<Article, Date> creationDate;
	public static volatile SingularAttribute<Article, Integer> progress;
	public static volatile SingularAttribute<Article, String> version;
	public static volatile SingularAttribute<Article, String> state;
	public static volatile SingularAttribute<Article, Date> lastUpdate;
	public static volatile SingularAttribute<Article, User> user;
	public static volatile SingularAttribute<Article, Demand> demand;
}
