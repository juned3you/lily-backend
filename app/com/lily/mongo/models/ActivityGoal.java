package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;

import com.lily.mongo.utils.Model;

@Embedded
public class ActivityGoal extends Model {
	
	public Integer activeMinutes;
	public Integer caloriesOut;
	public Float distance;	
	public Integer floors;	
	public Integer steps;

	public static Model.Finder<ObjectId, ActivityGoal> find() {
		return new Model.Finder<ObjectId, ActivityGoal>(ObjectId.class,
				ActivityGoal.class);
	}
}