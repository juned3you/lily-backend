package com.lily.mongo.models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import com.lily.mongo.utils.Model;

/**
 * Sleep log.
 * @author root
 *
 */
@Entity
public class SleepLog extends Model{
	
	@Id
	public ObjectId id;	
	
	@Reference
	public List<Sleep> sleep;
	
	@Reference
	public SleepSummary summary;
	
	public Date date;
	
	public String userId;
	
	public static Model.Finder<ObjectId, SleepLog> find() {
		return new Model.Finder<ObjectId, SleepLog>(ObjectId.class,
				SleepLog.class);
	}

}
