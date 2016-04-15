package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;

import com.lily.mongo.utils.Model;

@Embedded
public class ActivityLevel extends Model {
	public Integer id;
	public Integer maxSpeedMPH;
	public Integer minSpeedMPH;
	
	public Float mets;	
	public String name;	

	public static Model.Finder<ObjectId, ActivityLevel> find() {
		return new Model.Finder<ObjectId, ActivityLevel>(ObjectId.class,
				ActivityLevel.class);
	}
}