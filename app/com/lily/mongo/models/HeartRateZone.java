package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;

import com.lily.mongo.utils.Model;

@Embedded
public class HeartRateZone extends Model {
	
	
	public Float caloriesOut;

	public Integer max;
	public Integer min;
	public Integer minutes;
	public String name;

	public static Model.Finder<ObjectId, HeartRateZone> find() {
		return new Model.Finder<ObjectId, HeartRateZone>(ObjectId.class,
				HeartRateZone.class);
	}

}
