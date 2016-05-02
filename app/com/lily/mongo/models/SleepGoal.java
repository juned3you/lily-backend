package com.lily.mongo.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

/**
 * Sleep goal for user
 * 
 * @author Mohammad
 *
 */
public class SleepGoal extends Model {

	@Id
	public ObjectId id;
	public Integer minDuration;
	public Date updatedOn;
	public String userId;

	public static Model.Finder<ObjectId, SleepGoal> find() {
		return new Model.Finder<ObjectId, SleepGoal>(ObjectId.class,
				SleepGoal.class);
	}
}