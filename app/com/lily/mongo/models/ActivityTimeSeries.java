package com.lily.mongo.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

@Entity
public class ActivityTimeSeries extends Model {
	@Id
	public ObjectId id;
	public Date dateTime;
	public Float value;
	public String resourcePath;
	public String userId;

	public static Model.Finder<ObjectId, ActivityTimeSeries> find() {
		return new Model.Finder<ObjectId, ActivityTimeSeries>(ObjectId.class,
				ActivityTimeSeries.class);
	}
}
