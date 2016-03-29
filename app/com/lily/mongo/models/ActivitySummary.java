package com.lily.mongo.models;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

@Entity
public class ActivitySummary extends Model {
	@Id
	public ObjectId id;
	public Integer activeScore;
	public Integer activityCalories;
	public Integer caloriesBMR;
	public Integer caloriesOut;

	@Embedded
	public List<ActivityDistance> distances;

	public Float elevation;
	public Integer fairlyActiveMinutes;
	public Integer floors;
	public Integer lightlyActiveMinutes;

	public Integer marginalCalories;
	public Integer sedentaryMinutes;

	public Integer steps;
	public Integer veryActiveMinutes;

	public static Model.Finder<ObjectId, ActivitySummary> find() {
		return new Model.Finder<ObjectId, ActivitySummary>(ObjectId.class,
				ActivitySummary.class);
	}
}