package com.lily.mongo.models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import com.lily.mongo.utils.Model;

/**
 * Daily activities
 * 
 * @author Mohammad
 *
 */
public class DailyActivity extends Model {

	@Id
	public ObjectId id;
	
	@Reference
	public List<Activity> activities;

	@Embedded
	public ActivityGoal goals;

	@Reference
	public ActivitySummary summary;
	
	public String userId;
	
	public Date date;

	public static Model.Finder<ObjectId, DailyActivity> find() {
		return new Model.Finder<ObjectId, DailyActivity>(ObjectId.class,
				DailyActivity.class);
	}

}
