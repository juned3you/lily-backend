package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;

import com.lily.mongo.utils.Model;

@Embedded
public class ActivityDistance extends Model {

	public String activity;
	public Float distance;

	public static Model.Finder<ObjectId, ActivityDistance> find() {
		return new Model.Finder<ObjectId, ActivityDistance>(ObjectId.class,
				ActivityDistance.class);
	}
}
