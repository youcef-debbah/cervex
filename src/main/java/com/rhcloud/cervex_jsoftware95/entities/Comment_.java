package com.rhcloud.cervex_jsoftware95.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-04T15:48:33.721+0100")
@StaticMetamodel(Comment.class)
public class Comment_ {
	public static volatile SingularAttribute<Comment, String> commentID;
	public static volatile SingularAttribute<Comment, String> content;
	public static volatile SingularAttribute<Comment, Date> date;
	public static volatile SingularAttribute<Comment, Byte> authorized;
	public static volatile SingularAttribute<Comment, User> user;
	public static volatile SingularAttribute<Comment, Blog> blog;
}
