package com.rhcloud.cervex_jsoftware95.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-04-16T00:38:38.509+0100")
@StaticMetamodel(Demand.class)
public class Demand_ {
	public static volatile SingularAttribute<Demand, String> demandID;
	public static volatile SingularAttribute<Demand, Date> demandDate;
	public static volatile SingularAttribute<Demand, String> description;
	public static volatile SingularAttribute<Demand, String> title;
	public static volatile SingularAttribute<Demand, String> type;
	public static volatile SingularAttribute<Demand, Byte> state;
	public static volatile SingularAttribute<Demand, User> user;
	public static volatile SingularAttribute<Demand, Article> article;
	public static volatile ListAttribute<Demand, File> attachedFiles;
}
