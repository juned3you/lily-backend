package com.lily.mongo.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

public class HeartActivities extends Model {

	@Id
	public ObjectId id;

	public Date dateTime;

	public Integer restingHeartRate;

	@Embedded
	public HeartActivitiesValue value;
	
	public String userId;

	public static Model.Finder<ObjectId, HeartActivities> find() {
		return new Model.Finder<ObjectId, HeartActivities>(ObjectId.class,
				HeartActivities.class);
	}

}
