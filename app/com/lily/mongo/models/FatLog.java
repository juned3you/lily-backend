package com.lily.mongo.models;

import java.sql.Time;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

@Entity
public class FatLog extends Model {
	@Id
	public ObjectId id;
	public Long logId;

	public Float fat;
	public String source;

	public Date date;
	public Time time;
	
	public String userId;

	public static Model.Finder<ObjectId, FatLog> find() {
		return new Model.Finder<ObjectId, FatLog>(ObjectId.class, FatLog.class);
	}

}
