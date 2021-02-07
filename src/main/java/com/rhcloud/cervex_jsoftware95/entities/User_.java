package com.rhcloud.cervex_jsoftware95.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-04-18T13:51:36.078+0100")
@StaticMetamodel(User.class)
public class User_ {
	public static volatile SingularAttribute<User, String> userID;
	public static volatile SingularAttribute<User, Date> creationDate;
	public static volatile SingularAttribute<User, String> email;
	public static volatile SingularAttribute<User, String> enterpriseAddress;
	public static volatile SingularAttribute<User, String> enterpriseName;
	public static volatile SingularAttribute<User, String> password;
	public static volatile SingularAttribute<User, String> phoneNumber;
	public static volatile SingularAttribute<User, String> role;
	public static volatile SingularAttribute<User, String> username;
	public static volatile SingularAttribute<User, String> website;
	public static volatile ListAttribute<User, Article> articles;
	public static volatile ListAttribute<User, Demand> demands;
	public static volatile ListAttribute<User, Message> receivedMessages;
	public static volatile ListAttribute<User, Message> sendedMessages;
}
