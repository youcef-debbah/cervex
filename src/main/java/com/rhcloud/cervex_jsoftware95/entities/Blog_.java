package com.rhcloud.cervex_jsoftware95.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-03T12:20:35.696+0100")
@StaticMetamodel(Blog.class)
public class Blog_ {
	public static volatile SingularAttribute<Blog, String> blogID;
	public static volatile SingularAttribute<Blog, String> content;
	public static volatile SingularAttribute<Blog, String> intro;
	public static volatile SingularAttribute<Blog, Date> date;
	public static volatile SingularAttribute<Blog, String> title;
	public static volatile SingularAttribute<Blog, Byte> draft;
	public static volatile SingularAttribute<Blog, User> user;
	public static volatile ListAttribute<Blog, Comment> comments;
	public static volatile ListAttribute<Blog, File> attachedFiles;
	public static volatile SingularAttribute<Blog, File> thumbnail;
}
