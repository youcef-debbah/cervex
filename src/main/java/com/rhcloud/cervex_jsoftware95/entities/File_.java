package com.rhcloud.cervex_jsoftware95.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-04-15T20:28:22.225+0100")
@StaticMetamodel(File.class)
public class File_ {
	public static volatile SingularAttribute<File, String> fileID;
	public static volatile SingularAttribute<File, byte[]> contents;
	public static volatile SingularAttribute<File, String> contentType;
	public static volatile SingularAttribute<File, Date> uploadDate;
	public static volatile SingularAttribute<File, String> name;
	public static volatile SingularAttribute<File, Long> size;
	public static volatile SingularAttribute<File, Demand> demand;
	public static volatile SingularAttribute<File, Blog> blog;
}
