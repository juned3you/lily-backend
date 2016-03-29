package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

@Entity
public class Activity extends Model {
	@Id
	public ObjectId id;
	public Integer activityId;

	public Integer activityParentId;
	public Integer calories;
	public String description;
	public Float distance;
	public Long duration;
	public Boolean hasStartTime;
	public Boolean isFavorite;
	public Long logId;
	public String name;
	public String startTime;
	public Integer steps;

	public static Model.Finder<ObjectId, Activity> find() {
		return new Model.Finder<ObjectId, Activity>(ObjectId.class,
				Activity.class);
	}
}