package com.rhcloud.cervex_jsoftware95.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-03-29T00:11:19.532+0100")
@StaticMetamodel(Message.class)
public class Message_ {
	public static volatile SingularAttribute<Message, String> messageID;
	public static volatile SingularAttribute<Message, Byte> state;
	public static volatile SingularAttribute<Message, Date> sendingDate;
	public static volatile SingularAttribute<Message, String> text;
	public static volatile SingularAttribute<Message, String> title;
	public static volatile SingularAttribute<Message, User> receiver;
	public static volatile SingularAttribute<Message, User> sender;
}
